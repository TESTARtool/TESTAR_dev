import os
from flask import Blueprint, render_template

reporting = Blueprint(
    "reporting", __name__, template_folder="templates", static_folder="static"
)

if os.environ.get("ADAPTER") == "RANDOM":
    from .adapters.random_classes import Report
elif os.environ.get("ADAPTER") == "MYSQL":
    from .adapters.mysql_classes import Report, setup_db_pool

    setup_db_pool()
else:
    raise Exception("No adapter set")


@reporting.route("/")
def index():
    return render_template("index.html", reports=Report.get_reports())


@reporting.route("/report/<int:id>/")
def report_page(id):
    report = Report(id)
    return render_template("report.html", report=report)


@reporting.route("/sequence/<int:report_id>/<int:sequence_id>")
def sequence(report_id, sequence_id):

    # Retrieve report
    report = Report(report_id)

    # Retrieve given sequence
    sequence = report.get_sequence_by_id(sequence_id)
    return render_template("details.html", report=report, sequence=sequence)
