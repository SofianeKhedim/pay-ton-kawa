const express = require("express");
const mongoose = require("mongoose");
require("dotenv").config();
const orderRoutes = require("./routes/order.routes");
const { connectRabbitMQ } = require("./utils/rabbitmq");
const app = express();
app.use(express.json());


/* ──────────── 🆕  Prometheus client ──────────── */
const client = require("prom-client");
const collectDefaultMetrics = client.collectDefaultMetrics;
collectDefaultMetrics();                       // métriques système + Node

// ENDPOINT /metrics que Prometheus va scrapper
app.get("/metrics", async (req, res) => {
    try {
        res.set("Content-Type", client.register.contentType);
        res.end(await client.register.metrics());
    } catch (ex) {
        res.status(500).end(ex);
    }
});
/* ─────────────────────────────────────────────── */


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
