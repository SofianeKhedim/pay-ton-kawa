// services/productService.js
const axios = require("axios");

const PRODUCT_SERVICE_URL = "http://134.122.92.14:8089/api/product/all";

async function fetchAllProducts(req) {
  try {
    const authHeader = req.headers.authorization;
    
    if (!authHeader) {
      throw new Error("Token d'authentification manquant");
    }

    const response = await axios.get(PRODUCT_SERVICE_URL, {
      headers: {
        'Authorization': authHeader,
        'Content-Type': 'application/json'
      }
    });
    
    return response.data;
  } catch (error) {
    console.error("❌ Erreur lors de la récupération des produits :", error.message);
    
    if (error.response) {
      console.error("Status:", error.response.status);
      console.error("Response:", error.response.data);
    }
    
    return [];
  }
}


module.exports = { fetchAllProducts };