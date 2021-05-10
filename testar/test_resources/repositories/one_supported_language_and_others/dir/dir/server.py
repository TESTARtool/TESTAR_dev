import http.server
import time
import urllib.parse as urlparse
import socket


def check_correctness(direction_list):
    trans = 'go'
    rot_list = ['cw', 'ccw']
    try:
        for direction in direction_list:
            if any(type(v) != int for v in direction[1:]):
                raise TypeError
            if direction[0] == trans:
                if any((not 20 <= v <= 500) for v in direction[1:4]) | (not 10 <= direction[4] <= 100):
                    raise ValueError
            elif direction[0] in rot_list:
                if not 1 <= direction[1] <= 360:
                    raise ValueError
            else:
                raise ValueError
    except ValueError:
        print("Wrong command, distance, speed or angle value. See doc ;)")
        return 1
    except TypeError:
        print("Values must be integers")
        return 1


class DroneServerHTTP(http.server.BaseHTTPRequestHandler):
    def __init__(self):
        self.udp_ip = ""
        self.udp_port = 8888
        self.udpSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.udpSocket.bind((self.udp_ip, self.udp_port))

        # defining drone address
        # self.tello_ip = "192.168.10.1"
        self.tello_ip = "127.0.0.1"
        self.tello_port = 8889
        self.tello_address = (self.tello_ip, self.tello_port)


    def _set_headers(self):
        # responding to HTTP client that's okay and tell him what type of content he will get
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        # main content
        self._set_headers()
        # parsing url due to parameters and choose proper function
        go_to = urlparse.parse_qs(urlparse.urlparse(self.path).query).get("location", None)
        if go_to == ["A"]:
            self.trip_A()
        elif go_to == ["B"]:
            self.trip_B()
        elif go_to == ["C"]:
            self.trip_C()
        elif go_to == ["D"]:
            self.trip_D()
        else:
            self.wfile.write(bytes("<p>do_GET()<br>Parameter: %s</p>" % go_to, "utf-8"))

    def do_HEAD(self):
        # respond to client with header only
        self._set_headers()

    def do_POST(self):
        self._set_headers()
        self.wfile.write(bytes("<p>do_POST() method<br>path: %s</p>" % self.path, "utf-8"))

    def trip_A(self):
        # load commands from file and check if they're correct
        commands_file = open("tripA.txt", "r")
        commands_from_file = commands_file.readlines()
        # if check_correctness(commands_from_file) == 1:
        #     return

        # split file to separate commands and send them one by one
        for command in commands_from_file:
            if command != "" and command != "\n":
                command = command.rstrip()

                # find() returns -1 if desire string not found, lol
                if command.find("delay") != -1:
                    delay_time = float(command.partition("delay ")[2])
                    time.sleep(delay_time)
                    pass
                else:
                    self.udpSocket.sendto(command.encode("utf-8"), self.tello_address)

    def trip_B(self):
        output = "command\ntakeoff\nccw 360\nland"
        self.wfile.write(bytes(output, "utf-8"))

    def trip_C(self):
        output = "command\ntakeoff\ncw 360\nland"
        self.wfile.write(bytes(output, "utf-8"))

    def trip_D(self):
        output = "command\ntakeoff\nright 30\nleft 30\nland"
        self.wfile.write(bytes(output, "utf-8"))

    # def initialize_UDP_connection(self):
        # creating socket on server IP from which commands would be sent



# initialize server
myServer = http.server.HTTPServer(("", 8880), DroneServerHTTP)
print(time.asctime(), " Server Starts")

try:
    myServer.serve_forever()
except KeyboardInterrupt:
    pass

myServer.server_close()
print(time.asctime(), " Server Stops")
