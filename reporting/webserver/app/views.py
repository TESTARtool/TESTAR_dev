import os
from app import app
from flask import render_template

if os.environ.get('ADAPTER') == 'RANDOM':
    from .adapters.random_classes import *
elif os.environ.get('ADAPTER') == 'MYSQL':
      from .adapters.mysql_classes import *
else:
    raise Exception('No adapter set')


@app.route('/')
def index():
    return render_template('index.html', reports=Report.get_reports())


@app.route('/report/<int:id>/')
def report_page(id):
    report = Report.get_report_by_id(id)
    return render_template('report.html', report=report)


@app.route('/sequence/<int:report_id>/<int:sequence_id>')
def sequence(report_id, sequence_id):

    # Retrieve report
    report = Report.get_report_by_id(report_id)

    # Retrieve given sequence
    sequence = report.get_sequence_by_id(sequence_id)
    return render_template('details.html', report=report, sequence=sequence)
