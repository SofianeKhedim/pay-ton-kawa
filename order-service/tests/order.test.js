const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');
const Order = require('../src/models/order.model');
const {
  createOrder,
  getAllOrders,
  getOrderById,
  updateOrder,
  deleteOrder
} = require('../src/services/order.service');

let mongoServer;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  const uri = mongoServer.getUri();

  await mongoose.connect(uri, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  });
});

afterAll(async () => {
  await mongoose.connection.close();
  await mongoServer.stop();
});

afterEach(async () => {
  await Order.deleteMany();
});

describe('Order Service Unit Tests', () => {
  it('should create an order', async () => {
    const orderData = {
      clientId: 'abc123',
      products: [
        { productId: 'p1', quantity: 1, price: 10 }
      ]
    };

    const order = await createOrder(orderData);
    expect(order.clientId).toBe('abc123');
    expect(order.products.length).toBe(1);
  });

  it('should retrieve all orders', async () => {
    await createOrder({ clientId: '1', products: [{ productId: 'p1', quantity: 1, price: 5 }] });
    await createOrder({ clientId: '2', products: [{ productId: 'p2', quantity: 2, price: 15 }] });

    const orders = await getAllOrders();
    expect(orders.length).toBe(2);
  });

  it('should update an order', async () => {
    const order = await createOrder({ clientId: '3', products: [{ productId: 'p3', quantity: 1, price: 20 }] });

    const updated = await updateOrder(order._id, { orderStatus: 'shipped' });
    expect(updated.orderStatus).toBe('shipped');
  });

  it('should delete an order', async () => {
    const order = await createOrder({ clientId: '4', products: [{ productId: 'p4', quantity: 3, price: 30 }] });

    const deleted = await deleteOrder(order._id);
    expect(deleted.clientId).toBe('4');

    const check = await getOrderById(order._id);
    expect(check).toBeNull();
  });
});
