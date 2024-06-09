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
    print(f'Response: {response.read().decode()}\n')
    conn.close()

def random_id(): return str(uuid.uuid4())
def timestamp(): return datetime.now(timezone.utc).isoformat()
def random_float(): return round(random.uniform(50, 250), 2)
def email():
    name = ''.join(random.choices(string.ascii_letters, k=10))
    return f"{name}@domain.com"

item_pool =  [{"id": random_id(), "cost": random_float()} for _ in range(50)]


def create_order(user_id):
    do_post({
        "type": "USER_ORDER",
        "payload": {
            "userId": user_id,
            "orderId": random_id(),
            "items": random.sample(item_pool, random.randint(1,6)),
            "orderedAt": timestamp(),
        }
    })

def create_user(user_id):
    do_post({
        "type": "USER_CREATED",
        "payload": {
            "userId": user_id,
            "createdAt": timestamp(),
            "email": email()
        }
    })

def send_orders():
    user_pool = [random_id() for _ in range(3)]
    for user_id in user_pool:
        create_user(user_id)
    for _ in range(100):
        id = random.choice(user_pool)
        create_order(id);

send_orders()


