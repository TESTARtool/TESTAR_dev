import socket

host = '127.0.0.1'
port = 9000
# tello_IP = '192.168.10.1'
tello_IP = '127.0.0.1'
tello_command_port = 8889
tello_state_port = 8890
tello_stream_port = 11111

locaddr = (host, port)
tello_command_address = (tello_IP, tello_command_port)

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(locaddr)

'''
direction_list is two-dimensional list and contains lists:
['go', x_i, y_i, z_i, speed_i]
and
['cw', angle_i] or ['ccw', angle_i]

x_i, y_i, z_i = 20-500 [cm] integer
angle_i = 1-360 [degree] integer
speed_i = 10-100 [cm/s] integer

function send_path() converts them into command strings and sends them to Tello EDU

'''


def send_path_list(direction_list):
    trans = 'go'
    rot_list = ['cw', 'ccw']
    commands = list()
    try:
        for direction in direction_list:
            if any(type(v) != int for v in direction[1:]):
                raise TypeError
            if direction[0] == trans:
                if any((not 20 <= abs(v) <= 500) for v in direction[1:4]) | (not 10 <= direction[4] <= 100):
                    raise ValueError
                # commands.append(direction[0] + " " + str(direction[1]) + " " + str(direction[2]) + " " + str(direction[3] )+ " " + str(direction[4]))
                commands.append(bytearray(''.join([str(x) + ' ' for x in direction])[:-1], 'utf8'))

            elif direction[0] in rot_list:
                if not 1 <= direction[1] <= 360:
                    raise ValueError
                commands.append(bytearray(direction[0] + ' ' + str(direction[1]), 'utf8'))
            else:
                raise ValueError
    except ValueError:
        print("Wrong command, distance, speed or angle value. See doc ;)")
        return 1
    except TypeError:
        print("Values must be integers")
        return 1

    for c in commands:
        sock.sendto(c, tello_command_address)
    return 0

# def send_path_dict(direction_list):

#
# recvThread = threading.Thread(target=recv)
# recvThread.start()

# Create a UDP socket
