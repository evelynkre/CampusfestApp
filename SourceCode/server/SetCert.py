from socket import gethostname
from pprint import pprint
from time import gmtime, mktime
import os, subprocess

subprocess.call(["python","-m","pip","install","--upgrade","pip"])
subprocess.call(["pip", "install", "pyopenssl"])

from OpenSSL import crypto, SSL

CERT_FILE = os.path.join(os.getcwd(), "campusfestapp.crt")
KEY_FILE = os.path.join(os.getcwd(), "private.key")

def create_self_signed_cert():

    # create a key pair
    k = crypto.PKey()
    k.generate_key(crypto.TYPE_RSA, 1024)

    # create a self-signed cert
    cert = crypto.X509()
    cert.get_subject().C = "DE"
    cert.get_subject().ST = "Reutlingen"
    cert.get_subject().L = "Reutlingen"
    cert.get_subject().O = "CampusfestApp"
    cert.get_subject().OU = "CampusfestApp"
    cert.get_subject().CN = gethostname()
    cert.set_serial_number(1000)
    cert.gmtime_adj_notBefore(0)
    cert.gmtime_adj_notAfter(10*365*24*60*60)
    cert.set_issuer(cert.get_subject())
    cert.set_pubkey(k)
    cert.sign(k, 'sha256')

    open(CERT_FILE, "wt").write(
        crypto.dump_certificate(crypto.FILETYPE_PEM, cert))
    open(KEY_FILE, "wt").write(
        crypto.dump_privatekey(crypto.FILETYPE_PEM, k))
    


def main():
    create_self_signed_cert()
    print(gethostname())
    return


if __name__ == "__main__":
    main()
