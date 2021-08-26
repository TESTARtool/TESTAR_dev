from flask import Blueprint, render_template, jsonify
from statemodel.utils import OrientDB

statemodel = Blueprint(
    "statemodel",
    __name__,
    template_folder="templates",
    static_folder="static",
    static_url_path="/static/statemodel",
)

orientdb = OrientDB.from_env()

@statemodel.route("/concrete/state")
async def concrete_states_list():
    states = orientdb.concrete_states()
    return render_template("concrete_state_list.html", states=states)


@statemodel.route("/concrete/state/<string:cid>")
async def concrete_state_details(cid):
    state = orientdb.concrete_state_from_cid(cid)
    return render_template("concrete_state_detail.html", state=state)

############## API #############

@statemodel.route("/api/concrete/state/<string:cid>")
async def json_concrete_state_details(cid):
    return jsonify(orientdb.concrete_state_from_cid(cid))

@statemodel.route("/api/concrete/state")
async def json_concrete_state_list():
    return  jsonify(orientdb.concrete_states())