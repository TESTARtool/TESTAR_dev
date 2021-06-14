import os
from typing import List
from datetime import datetime
from mysql.connector import connect, Error


def setup_db_connection():
    """Create database connection from enviroment variables"""
    global db_connection

    # Extract settings
    config = {
        "host": os.environ.get("MYSQL_HOST", "localhost"),
        "port": int(os.environ.get("MYSQL_PORT", 13306)),
        "user": os.environ.get("MYSQL_USER", "testar"),
        "password": os.environ.get("MYSQL_PASSWORD", "testar"),
        "database": os.environ.get("MYSQL_DATABASE", "testar"),
        "raise_on_warnings": True,
    }

    db_connection = connect(**config)


def test():
    """Really simple test if the connection works"""
    print("Testing database connection..")
    setup_db_connection()

    print("Show the tables")
    cursor = db_connection.cursor()
    cursor.execute("SHOW TABLES;")
    for table in cursor:
        print(table)

    print("Closing the connection")
    cursor.close()
    db_connection.close()


if __name__ == "__main__":

    # Wait some time before trying to connect
    from time import sleep
    sleep(10)
    test()
