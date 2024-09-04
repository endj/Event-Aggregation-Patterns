use kafka::consumer::{Consumer, FetchOffset};
use serde_json::Value;
use std::collections::HashMap;
use std::error::Error;
mod models;
use models::*;

#[derive(Default)]
struct Stats {
    item_order_count: HashMap<String, usize>,
    user_order_count: HashMap<String, usize>,
    booked_balance: f64,
    settled_balance: f64,
    refunded_amount: f64
}

impl Stats {
    fn new() -> Self {
        Self::default()
    }

    fn reset(&mut self) {
        *self = Self::default()
    }

    fn log(&self) {
        for (user_id, count) in &self.user_order_count {
            println!("User ID: {}, Count: {}", user_id, count);
        }
        println!("Booked balance {:?}", self.booked_balance);
        println!("Settled balance {:?}", self.settled_balance);
        println!("Refunded Amount {:?}", self.refunded_amount);
    }
}

fn main() -> Result<(), Box<dyn Error>> {
    let hosts = vec!["localhost:9092".to_owned()];

    let mut consumer = Consumer::from_hosts(hosts)
        .with_topic("messages".to_owned())
        .with_fallback_offset(FetchOffset::Latest)
        .with_fetch_max_bytes_per_partition(1 << 20)
        .create()?;

    let mut stats = Stats::new();

    loop {
        for ms in consumer.poll()?.iter() {
            for m in ms.messages() {
                if let Err(err) = handle_message(m.value, &mut stats) {
                    eprintln!("Error handling message: {}", err);
                }
            }
            consumer.consume_messageset(ms)?;
            stats.log();
            stats.reset();
            println!("--------")
        }
        consumer.commit_consumed()?;
    }
}

fn handle_message(value: &[u8], stats: &mut Stats) -> Result<(), Box<dyn Error>> {
    let json: serde_json::value::Value = serde_json::from_slice(value)?;
    let message_type = json
        .get("type")
        .and_then(|t| t.as_str())
        .ok_or("Could not find type")?;

    let payload = json.get("payload").ok_or( "Missing payload")?;

    match message_type {
        "USER_CREATED" => handle_user_creation(payload)?,
        "USER_DELETED" => handle_delete_user(payload)?,
        "USER_ORDER" => handle_user_order(payload, stats)?,
        "USER_PAYMENT" => handle_user_purchase(payload, stats)?,
        "USER_REFUND" => handle_user_refund(payload, stats)?,
        _ => println!("Invalid type {}", message_type),
    }
    Ok(())
}
fn handle_user_creation(payload: &Value) -> Result<(), Box<dyn Error>> {
    let _created_user: UserCreated = serde_json::from_value(payload.clone())?;
    Ok(())
}

fn handle_delete_user(payload: &Value) -> Result<(), Box<dyn Error>> {
    let _delete_user: UserDeleted = serde_json::from_value(payload.clone())?;
    Ok(())
}

fn handle_user_order(payload: &Value, stats: &mut Stats) -> Result<(), Box<dyn Error>> {
    let user_order: UserOrder = serde_json::from_value(payload.clone())?;

    for item in user_order.items {
        *stats.user_order_count.entry(user_order.user_id.clone()).or_insert(0) += 1;
        *stats.item_order_count.entry(item.id).or_insert(0) += 1;
        stats.booked_balance += item.cost;
    }

    Ok(())
}

fn handle_user_purchase(payload: &Value, stats: &mut Stats) -> Result<(), Box<dyn Error>> {
    let user_payment: PaymentConfirmed = serde_json::from_value(payload.clone())?;
    stats.settled_balance += user_payment.amount;
    Ok(())
}

fn handle_user_refund(payload: &Value, stats: &mut Stats) -> Result<(), Box<dyn Error>> {
    let user_refund: PaymentRefunded = serde_json::from_value(payload.clone())?;
    stats.refunded_amount += user_refund.refund_amount;
    Ok(())
}