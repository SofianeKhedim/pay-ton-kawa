
const Order = require('../models/order.model');

async function createOrder(data) {
  const order = new Order(data);
  return await order.save();
}

async function getAllOrders() {
  return await Order.find();
}

async function getOrderById(id) {
  return await Order.findById(id);
}

async function updateOrder(id, data) {
  return await Order.findByIdAndUpdate(id, data, { new: true });
}

async function deleteOrder(id) {
  return await Order.findByIdAndDelete(id);
}

module.exports = {
  createOrder,
  getAllOrders,
  getOrderById,
  updateOrder,
  deleteOrder
};
