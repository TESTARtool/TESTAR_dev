from flask import Flask
import os

app = Flask(__name__)
# Register reporter
if os.environ.get("ADAPTER") in ["RANDOM", "MYSQL"]:
    from app.reporting.views import reporting

    app.register_blueprint(reporting)


# Register statemodel previewer
if bool(os.environ.get("ORIENTDB_ENABLED", "0")):
    from app.statemodel.views import statemodel

    app.register_blueprint(statemodel)
