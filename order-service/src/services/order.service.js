const Order = require("../models/order.model");
const { publishOrderEvent } = require("../utils/rabbitmq");
const { fetchAllProducts } = require("./product.service");

async function createOrder(data) {
  const order = new Order(data);
  const saved = await order.save();
  publishOrderEvent(saved);
  return saved;
}

async function getAllOrders() {
  const orders = await Order.find({}, { "products.quantity": 0 }).lean();
  const allProducts = await fetchAllProducts();
// console.log("all pr info : ",allProducts)
  const enrichedOrders = orders.map(order => {
    const enrichedProducts = order.products.map(prod => {
      const productInfo = allProducts.find(p => String(p.id) === String(prod.productId));
      // console.log("product info : ",productInfo)
      return {
        ...prod,
        name: productInfo?.name || "Nom inconnu",
        description: productInfo?.description || "Pas de description",
        price: productInfo?.price || "Pas de price",
        quantity: productInfo?.quantity || "Pas de quantity",
      };
    });

    return {
      ...order,
      products: enrichedProducts,
    };
  });

  return enrichedOrders;
}


async function getOrderById(id) {
  const order = await Order.findById(id,{"products.quantity": 0}).lean();
  if (!order) return null;

  const allProducts = await fetchAllProducts();

  const enrichedProducts = order.products.map(prod => {
    const productInfo = allProducts.find(p => String(p.id) === String(prod.productId));
    return {
      ...prod,
      name: productInfo?.name || "Nom inconnu",
      description: productInfo?.description || "Pas de description",
    };
  });

  return {
    ...order,
    products: enrichedProducts,
  };
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
  deleteOrder,
};
