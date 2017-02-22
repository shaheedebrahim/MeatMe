# Flask imports
from flask import Flask
from flask_mysqldb import MySQL
from flask_restful import reqparse, Resource, Api

# Google API imports
from apiclient import discovery
import httplib2
from oauth2client import client

# Other imports
import datetime, logging, uuid

app = Flask(__name__)
app.config['MYSQL_USER'] = 'root'
app.config['MYSQL_PASSWORD'] = open('/home/bandaids/MeatMe/server/mysql-root-password').read().strip()
app.config['MYSQL_DB'] = 'bandaids'

api = Api(app)
mysql = MySQL(app)

# Set path to the Web application client_secret_*.json file you downloaded from the
# Google API Console: https://console.developers.google.com/apis/credentials
CLIENT_SECRET_FILE = '/home/bandaids/MeatMe/server/client_secret_706715075615-tgl40g50i13uf9enegi3i13pmp2c7ic0.apps.googleusercontent.com.json'

class LoginGoogle(Resource):
    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        self.reqparse.add_argument('authCode', type=str, required=True, location='form')
    #def get(self):
        # Testing endpoint
        #logging.info("Hello World Info")
        #return {'response':'Hello World'}
        #flow = client.flow_from_clientsecrets(
        #        CLIENT_SECRET_FILE,
        #        scope=['https://www.googleapis.com/auth/calendar.readonly', 'profile', 'email'],
        #        redirect_uri='https://bandaids.northcentralus.cloudapp.azure.com/')
        #return {'auth_url': flow.step1_get_authorize_url()}
        #credentials = flow.step2_exchange('TOKEN')
        #http_auth = credentials.authorize(httplib2.Http())
        #service = discovery.build('plus', 'v1', http=http_auth)
        #people_resource = service.people()
        #people_document = people_resource.get(userId='me').execute()
        #return {'people_document': people_document}

    def post(self):
        args = self.reqparse.parse_args()

        # Exchange auth code for access token, refresh token, and ID token
        credentials = client.credentials_from_clientsecrets_and_code(
            filename=CLIENT_SECRET_FILE,
            scope=['https://www.googleapis.com/auth/calendar', 'profile', 'email'],
            code=args['authCode'],
            redirect_uri='urn:ietf:wg:oauth:2.0:oob')

        # Get profile info
        http_auth = credentials.authorize(httplib2.Http())
        service = discovery.build('plus', 'v1', http=http_auth)
        people_resource = service.people()
        people_document = people_resource.get(userId='me').execute()
        googleid = credentials.id_token['sub']
        email = credentials.id_token['email']
        name = people_document['displayName']
        firstname = people_document['name']['givenName']
        lastname = people_document['name']['familyName']
        oauth2credentials = credentials.to_json()

        # Determine if user is already in the database
        c = mysql.connection.cursor()
        c.execute("SELECT user_id FROM user_login WHERE user_login_site='google' AND user_login_id=%s", (googleid,))
        user_login = c.fetchone()
        if user_login is None:
            # User does not exist, create new users
            c.execute("INSERT INTO user (user_name, user_first_name, user_last_name, user_email) VALUES (%s, %s, %s, %s)", (name, firstname, lastname, email))
            user_id = c.lastrowid
            c.execute("INSERT INTO user_login (user_login_site, user_login_id, user_id, user_login_oauth2credentials) VALUES ('google', %s, %s, %s)", (googleid, user_id, oauth2credentials))
        else:
            # User exists, update credentials
            c.execute("UPDATE user_login SET user_login_oauth2credentials=%s WHERE user_login_site='google' AND user_login_id=%s", (oauth2credentials, googleid))
            user_id = user_login[0]

        # Create session
        session_id = str(uuid.uuid4())
        c.execute("INSERT INTO session (session_id, user_id) VALUES (%s, %s)", (session_id, user_id))
        mysql.connection.commit()

        return {'session_id': session_id, 'user_id': user_id}

class NearbyUsers(Resource):
    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        self.reqparse.add_argument('session_id', type=str, required=True, location='form')
        self.reqparse.add_argument('lat', type=float, required=True, location='form')
        self.reqparse.add_argument('lon', type=float, required=True, location='form')
    def post(self):
        args = self.reqparse.parse_args()
        lat = args['lat']
        lon = args['lon']

        # Look up session ID
        c = mysql.connection.cursor()
        c.execute("SELECT user_id FROM session WHERE session_id=%s", (args['session_id'],))
        session = c.fetchone()
        if session is None:
            raise RuntimeError("Invalid session")
        user_id = session[0]

        # Update user position in database
        c.execute("UPDATE user SET user_position_lat=%s, user_position_lon=%s, user_position_time=NOW() WHERE user_id=%s", (lat, lon, user_id))

        # Find near neighbors
        c.execute("SELECT user_id, user_name, user_first_name, user_last_name, ( 6371 * acos( cos( radians(%s) ) * cos( radians(user_position_lat) ) * cos( radians(user_position_lon) - radians(%s) ) + sin( radians(%s) ) * sin(radians(user_position_lat)) ) ) AS distance FROM user WHERE user_position_time > (NOW() - INTERVAL 30 SECOND) HAVING distance < 0.01 LIMIT 20", (lat, lon, lat))
        result = []
        while True:
            row = c.fetchone()
            if row is None: break
            result.append({"user_id":row[0], "user_name":row[1], "user_first_name":row[2], "user_last_name":row[3], "distance": row[4]})
        mysql.connection.commit()
        return {'users': result}

class UsersFreeBusy(Resource):
    def __init__(self):
        self.reqparse = reqparse.RequestParser()
        self.reqparse.add_argument('session_id', type=str, required=True, location='form')
        self.reqparse.add_argument('user_ids', type=int, required=True, location='form', action='append')
        self.reqparse.add_argument('min_time', type=int, required=True, location='form')
        self.reqparse.add_argument('max_time', type=int, required=True, location='form')
    def post(self):
        args = self.reqparse.parse_args()

        # Format times as desired by Google Calendar API
        min_time = datetime.datetime.utcfromtimestamp(args['min_time']).isoformat() + 'Z'
        max_time = datetime.datetime.utcfromtimestamp(args['max_time']).isoformat() + 'Z'

        # Look up session ID
        c = mysql.connection.cursor()
        c.execute("SELECT user_id FROM session WHERE session_id=%s", (args['session_id'],))
        session = c.fetchone()
        if session is None:
            raise RuntimeError("Invalid session")
        user_id = session[0]

        # Get data from Google API
        api_result = {}
        for user_id in args['user_ids']:
            c.execute("SELECT user_login_oauth2credentials FROM user_login WHERE user_id=%s AND user_login_site='google' LIMIT 1", (user_id,))
            user = c.fetchone()
            if user is None:
                raise RuntimeError("Invalid user")
            try:
                credentials = client.OAuth2Credentials.from_json(user[0])
                http_auth = credentials.authorize(httplib2.Http())
                service = discovery.build('calendar', 'v3', http=http_auth)
                result = service.freebusy().query(body={'timeMin':min_time, 'timeMax':max_time, 'items':[{"id":"primary"}]}).execute()
                busy = result.get('calendars', {}).get('primary', {}).get('busy', [])
            except client.Error as e:
                logging.warning('OAuth client error on user %s: %s' % (user_id, e))
                continue
            range_result = []
            for time_range in busy:
                start_time = datetime.datetime.strptime(time_range['start'], "%Y-%m-%dT%H:%M:%SZ")
                end_time = datetime.datetime.strptime(time_range['end'], "%Y-%m-%dT%H:%M:%SZ")
                range_result.append({'start_time': int(start_time.timestamp()), 'end_time': int(end_time.timestamp())})
            api_result[str(user_id)] = range_result
        return api_result

api.add_resource(LoginGoogle, '/login_google')
api.add_resource(NearbyUsers, '/nearby_users')
api.add_resource(UsersFreeBusy, '/users_free_busy')

if __name__ == '__main__':
    app.run(debug=True)
