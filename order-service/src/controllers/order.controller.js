const {
  createOrder,
  getAllOrders,
  getOrderById,
  updateOrder,
  deleteOrder
} = require('../services/order.service');

async function create(req, res) {
  try {
    const order = await createOrder(req.body);
    res.status(201).json(order);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
}


async function findAll(req, res) {
  try {
    const orders = await getAllOrders(
      req
    );
    res.json(orders);
  } catch (err) {
    res.status(500).json({ error: err.message } );
  }
}

async function findOne(req, res) {
  try {
    const order = await getOrderById(req.params.id);
    if (!order) return res.status(404).json({ message: 'Commande non trouvée' });
    res.json(order);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}

async function update(req, res) {
  try {
    const order = await updateOrder(req.params.id, req.body);
    if (!order) return res.status(404).json({ message: 'Commande non trouvée' });
    res.json(order);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
}

async function remove(req, res) {
  try {
    const order = await deleteOrder(req.params.id);
    if (!order) return res.status(404).json({ message: 'Commande non trouvée' });
    res.json({ message: 'Commande supprimée' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}

module.exports = {
  create,
  findAll,
  findOne,
  update,
  remove
};
