use serde::{Deserialize, Serialize};


#[derive(Debug, Serialize, Deserialize)]
pub struct UserCreated {
    #[serde(rename = "userId")]
    pub user_id: String,
    #[serde(rename = "createdAt")]
    pub created_at: String,
    pub email: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UserDeleted {
    #[serde(rename = "userId")]
    pub user_id: String,
    #[serde(rename = "deletedAt")]
    pub deleted_at: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Item {
    pub id: String,
    pub cost: f64
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UserOrder {
    #[serde(rename = "userId")]
    pub user_id: String,
    #[serde(rename = "orderId")]
    pub order_id: String,
    pub items: Vec<Item>,
    #[serde(rename = "orderedAt")]
    pub ordered_at: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct PaymentRefunded {
    #[serde(rename = "orderId")]
    pub order_id: String,
    #[serde(rename = "userId")]
    pub user_id: String,
    #[serde(rename = "refundedAt")]
    pub refunded_at: String,
    #[serde(rename = "refundAmount")]
    pub refund_amount: f64
}

#[derive(Debug, Serialize, Deserialize)]
pub struct PaymentConfirmed {
    #[serde(rename = "userId")]
    pub user_id: String,

    #[serde(rename = "orderId")]
    pub order_id: String,

    #[serde(rename = "amount")]
    pub amount: f64,

    #[serde(rename = "payedAt")]
    pub created_at: String,
}
