const express = require('express');
const router = express.Router();
const orderController = require('../controllers/order.controller');

// Routes CRUD pour les commandes
router.post('/orders', orderController.create);       // Créer une commande
router.get('/orders', orderController.findAll);       // Lister toutes les commandes
router.get('/orders/:id', orderController.findOne);   // Détails d’une commande
router.put('/orders/:id', orderController.update);    // Modifier une commande
router.delete('/orders/:id', orderController.remove); // Supprimer une commande

module.exports = router;
