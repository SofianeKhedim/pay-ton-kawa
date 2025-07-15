// services/productService.js
const axios = require("axios");

const PRODUCT_SERVICE_URL = "http://localhost:8089/api/product/all";

async function fetchAllProducts() {
  try {
    const response = await axios.get(PRODUCT_SERVICE_URL);
    console.log("Produits récupérés :", response.data); // log des produits
    return response.data;
  } catch (error) {
    console.error("Erreur lors de la récupération des produits :", error.message);
    return [];
  }
}

module.exports = { fetchAllProducts };
