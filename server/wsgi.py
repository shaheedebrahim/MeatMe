activate_this = '/home/bandaids/MeatMe/server/venv/bin/activate_this.py'
with open(activate_this) as file_:
    exec(file_.read(), dict(__file__=activate_this))
import sys
sys.path.append('/home/bandaids/MeatMe/server')
from api import app as application
