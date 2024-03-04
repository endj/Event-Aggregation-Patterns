use kafka::consumer::{Consumer, FetchOffset};
use std::collections::HashMap;
use std::error::Error;
use std::os::macos::raw::stat;
use std::thread;
use std::time::Duration;
mod models;
use models::*;

struct Stats {
    item_order_count: HashMap<String, usize>,
    user_order_count: HashMap<String, usize>,
    booked_balance: f64,
    settled_balance: f64,
    refunded_amount: f64
}

fn main() -> Result<(), Box<dyn Error>> {
    let hosts = vec!["localhost:9092".to_owned()];

    let mut consumer = Consumer::from_hosts(hosts)
        .with_topic("test".to_owned())
        .with_fallback_offset(FetchOffset::Latest)
        .with_fetch_max_bytes_per_partition(1 << 20)
        .create()?;

    let mut stats = Stats {
        item_order_count: HashMap::new(),
        user_order_count: HashMap::new(),
        booked_balance: 0.0,
        settled_balance: 0.0,
        refunded_amount: 0.0
    };

    loop {
        for ms in consumer.poll()?.iter() {
            for m in ms.messages() {
                if let Err(err) = handle_message(m.value, &mut stats) {
                    eprintln!("Error handling message: {}", err);
                }
            }
            consumer.consume_messageset(ms)?;
            println!("-- Batch done --");
        
            log_reset_stats(&mut stats);
            println!("--------")
        }
        consumer.commit_consumed()?;
    }
}

fn log_reset_stats(stats: &mut Stats) {
    for (user_id, count) in &stats.user_order_count {
        println!("User ID: {}, Count: {}", user_id, count);
    }
    println!("Booked balance {:?}", stats.booked_balance);
    println!("Settled balance {:?}", stats.settled_balance);
    println!("Refunded Amount {:?}", stats.refunded_amount);
    stats.item_order_count.clear();
    stats.user_order_count.clear();
    stats.booked_balance = 0.0;
    stats.settled_balance = 0.0;
    stats.refunded_amount = 0.0;
}

fn handle_message(value: &[u8], stats: &mut Stats) -> Result<(), Box<dyn Error>> {
    let json: serde_json::value::Value = serde_json::from_slice(value).unwrap();
    let message_type = json
        .get("type")
        .and_then(|t| t.as_str())
        .ok_or_else(|| "Could not find type")?;

    let payload = json.get("payload").ok_or("Missing payload")?;

    match message_type {
        "USER_CREATED" => handle_user_creation(payload),
        "USER_DELETED" => handle_delete_user(payload),
        "USER_ORDER" => handle_user_order(payload, stats),
        "USER_PAYMENT" => handle_user_purchase(payload, stats),
        "USER_REFUND" => handle_user_refund(payload, stats),
        _ => println!("Invalid type {}", message_type),
    }
    let duration = Duration::from_millis(20);
    thread::sleep(duration);
    Ok(())
}
fn handle_user_creation(payload: &serde_json::value::Value) {
    let created_user: UserCreated = serde_json::from_value(payload.clone()).unwrap();
    println!("Created user {:?}", created_user.user_id);
}

fn handle_delete_user(payload: &serde_json::value::Value) {
    let delete_user: UserDeleted = serde_json::from_value(payload.clone()).unwrap();
    println!("Deleted user {:?}", delete_user);
}

fn handle_user_order(payload: &serde_json::value::Value, stats: &mut Stats) {
    let user_order: UserOrder = serde_json::from_value(payload.clone()).unwrap();
    println!("User order: order: [{}] user: [{}]", user_order.order_id, user_order.user_id);

    for item in user_order.items {
        // count how many times an item id shows up 
        *stats.user_order_count.entry(user_order.user_id.clone()).or_insert(0) += 1;
        *stats.item_order_count.entry(item.id).or_insert(0) += 1;
        stats.booked_balance += item.cost;
    }
}

fn handle_user_purchase(payload: &serde_json::value::Value, stats: &mut Stats) {
    let user_payment: PaymentConfirmed = serde_json::from_value(payload.clone()).unwrap();
    println!("User Payment {:?}", user_payment);
    stats.settled_balance += user_payment.amount;
}

fn handle_user_refund(payload: &serde_json::value::Value, stats: &mut Stats) {
    let user_refund: PaymentRefunded = serde_json::from_value(payload.clone()).unwrap();
    println!("User Refund {:?}", user_refund);
    stats.refunded_amount += user_refund.refound_amount;
}
