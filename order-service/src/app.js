const express = require("express");
const mongoose = require("mongoose");
require("dotenv").config();
const orderRoutes = require("./routes/order.routes");
const { connectRabbitMQ } = require("./utils/rabbitmq");
const app = express();
app.use(express.json());

connectRabbitMQ()
  .then(() => console.log("✅ Connected to RabbitMQ"))
  .catch((err) => console.error("❌ RabbitMQ connection error:", err));

mongoose
  .connect(process.env.MONGODB_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  })
  .then(() => console.log("MongoDB connected"))
  .catch((err) => console.error("MongoDB connection error:", err));

app.use("/api", orderRoutes);

module.exports = app;

if (require.main === module) {
  const PORT = process.env.PORT || 3000;
  app.listen(PORT, () => {
    console.log(`Order service listening on port ${PORT}`);
  });
}
