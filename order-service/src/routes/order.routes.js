const express = require('express');
const router = express.Router();
const orderController = require('../controllers/order.controller');
const { authenticateToken, requireAdmin } = require('../middleware/auth');

// Health check - Public (AVANT les middlewares protégés)
router.get('/health', (req, res) => {
    res.json({
        status: 'UP',
        service: 'order-api',
        timestamp: new Date().toISOString(),
        version: '1.0'
    });
});

// Routes protégées par JWT
router.get('/orders', authenticateToken, orderController.findAll);
router.get('/orders/:id', authenticateToken, orderController.findOne);
router.post('/orders', authenticateToken, orderController.create);
router.put('/orders/:id', authenticateToken, orderController.update);

// Route admin uniquement
router.delete('/orders/:id', authenticateToken, requireAdmin, orderController.remove);

module.exports = router;