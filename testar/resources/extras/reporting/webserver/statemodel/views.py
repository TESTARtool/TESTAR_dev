from flask import Blueprint, render_template, jsonify
from statemodel.utils import (
    concrete_states,
    concrete_state_from_cid,
    concrete_state_traverse,
)

statemodel = Blueprint(
    "statemodel",
    __name__,
    template_folder="templates",
    static_folder="static",
    static_url_path="/static/statemodel",
)


@statemodel.route("/concrete/")
async def view_concrete_states_list():
    states = concrete_states()
    return render_template("concrete_state_list.html", states=states)


@statemodel.route("/concrete/<string:cid>")
async def view_concrete_state_details(cid):
    state = concrete_state_from_cid(cid)
    return render_template("concrete_state_detail.html", state=state)


@statemodel.route("/concrete/traverse/<string:from_cid>/<string:to_cid>")
async def view_concrete_state_traverse(from_cid, to_cid):
    return render_template(
        "concrete_state_traverse.html", from_cid=from_cid, to_cid=to_cid
    )


############## API #############


@statemodel.route("/api/concrete/<string:cid>")
async def json_concrete_state_details(cid):
    state = concrete_state_from_cid(cid)

    if state:
        return jsonify(state)

    return jsonify({"message": "State not found or misses information."})


@statemodel.route("/api/concrete/")
async def json_concrete_state_list():
    state = concrete_states()
    if state:
        return jsonify(state)
    else:
        return jsonify({"message": "No states found."})


@statemodel.route("/api/concrete/traverse/<string:from_cid>/<string:to_cid>")
async def json_state_traverse(from_cid, to_cid):

    # Don't request items with the same ConcreteID
    if from_cid == to_cid:
        return jsonify({"message": "from_cid & to_cid are the same"})

    path = concrete_state_traverse(from_cid, to_cid)

    # Only return path when it exists
    if path:
        return jsonify(path)

    # Return error message if there is no path between 2 states
    else:
        return jsonify({"message": f"No path found from `{from_cid}` to `{to_cid}`."})
