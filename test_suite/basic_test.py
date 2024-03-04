import http.client
import json
import uuid
import random
import string
from datetime import datetime, timezone

def do_post(json_payload):
    conn = http.client.HTTPConnection("localhost:80")
    headers = {'Content-type': 'application/json'}
    json_string = json.dumps(json_payload)
    conn.request(method="POST", url="/msg", body=json_string, headers=headers)
    response = conn.getresponse()
    print(f"Status: {response.status}")
    print(f'Response: {response.read().decode()}')
    conn.close()

def random_id(): return str(uuid.uuid4())
def timestamp(): return datetime.now(timezone.utc).isoformat()
def random_float(): return round(random.uniform(50, 250), 2)
def email():
    name = ''.join(random.choices(string.ascii_letters, k=10))
    return f"{name}@domain.com"


def delete_user():
    do_post({
        "type": "USER_DELETED",
        "payload": {
            "userId": random_id(),
            "deletedAt": timestamp()
        }
    })

def issue_refund():
    do_post({
        "type": "USER_REFUND",
        "payload": {
            "orderId": random_id(),
            "userId": random_id(),
            "refundedAt": timestamp(),
            "refundAmount": random_float()
        }
    })

def create_payment():
    do_post({
        "type": "USER_PAYMENT",
        "payload": {
            "userId": random_id(),
            "orderId": random_id(),
            "amount": random_float(),
            "payedAt": timestamp(),
        }
    })

def create_order():
    do_post({
        "type": "USER_ORDER",
        "payload": {
            "userId": random_id(),
            "orderId": random_id(),
            "items": [{"id": random_id(), "cost": random_float()} for _ in range(random.randint(5, 10))],
            "orderedAt": timestamp(),
        }
    })

def create_user():
    do_post({
        "type": "USER_CREATED",
        "payload": {
            "userId": random_id(),
            "createdAt": timestamp(),
            "email": email()
        }
    })

def run_suite():
    create_user()
    create_order()
    create_payment()
    issue_refund()
    delete_user()

run_suite()