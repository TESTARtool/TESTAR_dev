import pytest
import drone_controller as dc
import socket
import time
import threading


def wrapper_send_path_list(direction_list):
    time.sleep(0.5)
    dc.send_path_list(direction_list)


def test_send_path_list_1():
    direction_list_1 = [
        ['go', 50, 120, 50, 25],
        ['go', 380, 20, 500, 100],
        ['cw', 1],
        ['ccw', 200],
        ['ccw', 360],
        ['go', 20, 20, 20, 10],
        ['go', 500, 500, 500, 100]
    ]

    recvThread = threading.Thread(target=wrapper_send_path_list, args=[direction_list_1])
    recvThread.start()

    host = '127.0.0.1'
    port = 8889
    locaddr = (host, port)

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind(locaddr)
    sock.settimeout(1)

    received_list = list()
    start_time = time.time()
    while True:
        if time.time() - start_time > 20:
            break
        try:
            data, server = sock.recvfrom(1518)
            # print(data.decode(encoding="utf-8"))
            received_list.append(data.decode(encoding="utf-8"))
        except Exception:
            print('\nExit . . .\n')
            break

    direction_string = 'go 50 120 50 25go 380 20 500 100cw 1ccw 200ccw 360go 20 20 20 10go 500 500 500 100'

    assert direction_string == ''.join(received_list)

def test_send_path_list_2():
    direction_list_2 = [
        ['go', 501, 120, 50, 25],
        ['go', 380, 20, 500, 100],
        ['cw', 1],
        ['ccw', 200],
        ['ccw', 360],
        ['go', 20, 20, 20, 10],
        ['go', 500, 500, 500, 100]
    ]
    assert dc.send_path_list(direction_list_2)==1


def test_send_path_list_3():
    direction_list_3 = [
        ['go', 50, 120, 50, 25],
        ['go', 380, 20, 500, 100],
        ['cw', 1],
        ['ccw', 200],
        ['ccw', 0],
        ['go', 20, 20, 20, 10],
        ['go', 500, 500, 500, 100]
    ]
    assert dc.send_path_list(direction_list_3)==1

def test_send_path_list_4():
    direction_list_4 = [
        ['go', 50, 120, 50, 25],
        ['go', 380, 20, 500, 100],
        ['vw', 1],
        ['ccw', 200],
        ['ccw', 140],
        ['go', 20, 20, 20, 10],
        ['go', 500, 500, 500, 100]
    ]
    assert dc.send_path_list(direction_list_4)==1
