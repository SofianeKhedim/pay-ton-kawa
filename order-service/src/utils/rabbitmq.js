const amqp = require('amqplib');

let channel;

async function connectRabbitMQ(retries = 5, delay = 3000) {
  while (retries > 0) {
    try {
      const connection = await amqp.connect('amqp://rabbitmq');
      channel = await connection.createChannel();
      await channel.assertQueue('order_events');
      console.log('✅ Connected to RabbitMQ');
      break;
    } catch (err) {
      console.error(`❌ RabbitMQ connection error: ${err.message}`);
      retries--;
      if (retries === 0) {
        console.error('💥 Failed to connect to RabbitMQ after several attempts');
        process.exit(1);
      }
      console.log(`🔁 Retrying in ${delay / 1000}s... (${retries} tries left)`);
      await new Promise((resolve) => setTimeout(resolve, delay));
    }
  }
}


function publishOrderEvent(order) {
  if (!channel) throw new Error('RabbitMQ channel not initialized');

  const message = {
    event: 'order_created',
    data: {
      orderId: order._id,
      clientId: order.clientId,
      products: order.products,  
      orderStatus: order.orderStatus,
      createdAt: order.createdAt,
    }
  };

  channel.sendToQueue('order_events', Buffer.from(JSON.stringify(message)));
}


module.exports = {
  connectRabbitMQ,
  publishOrderEvent
};
