SET echo TRUE
SET ignoreErrors FALSE

CREATE DATABASE remote:localhost/testar root testar PLOCAL
CONNECT remote:localhost/testar root testar
CREATE USER testar IDENTIFIED BY testar ROLE admin