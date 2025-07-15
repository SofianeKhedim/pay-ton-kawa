const amqp = require("amqplib");
const Order = require("../models/order.model");
let channel;

async function connectRabbitMQ(retries = 5, delay = 3000) {
  while (retries > 0) {
    try {
      const connection = await amqp.connect("amqp://rabbitmq:5672");
      channel = await connection.createChannel();
      await channel.assertQueue("order_events");
      await channel.assertQueue("stock_events");
      console.log("✅ Connected to RabbitMQ");
      channel.consume("stock_events", async (msg) => {
        if (msg !== null) {
          const content = JSON.parse(msg.content.toString());
          console.log("📥 Réponse stock reçue :", content);

          const { orderId } = content.data;
          const event = content.event;

          let newStatus = "pending";
          if (event === "stock_validated") newStatus = "validated";
          if (event === "stock_failed") newStatus = "failed";

          await Order.findByIdAndUpdate(orderId, { orderStatus: newStatus });
          console.log(
            `🔄 Statut commande ${orderId} mis à jour : ${newStatus}`
          );

          channel.ack(msg) ; 
        }
      });

      break;
    } catch (err) {
      console.error(`❌ RabbitMQ connection error: ${err.message}`);
      retries--;
      if (retries === 0) {
        console.error(
          "💥 Failed to connect to RabbitMQ after several attempts"
        );
        process.exit(1);
      }
      console.log(`🔁 Retrying in ${delay / 1000}s... (${retries} tries left)`);
      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }
}

function publishOrderEvent(order) {
  if (!channel) throw new Error("RabbitMQ channel not initialized");

  const message = {
    event: "order_created",
    data: {
      orderId: order._id,
      clientId: order.clientId,
      products: order.products,
      orderStatus: order.orderStatus,
      createdAt: order.createdAt,
    },
  };

  channel.sendToQueue("order_events", Buffer.from(JSON.stringify(message)));
}

module.exports = {
  connectRabbitMQ,
  publishOrderEvent,
};
