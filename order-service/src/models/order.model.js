const mongoose = require("mongoose");

const orderSchema = new mongoose.Schema({
  clientId: "string",
  products: [{ productId: "string", quantity: Number, price: Number }],
  orderStatus: { type: String, default: "pending" },
  createdAt: Date,
});


// // Virtual for order age in days
// orderSchema.virtual("orderAge").get(function () {
//   return Math.floor((Date.now() - this.createdAt) / (1000 * 60 * 60 * 24));
// });

// // Method to calculate totals
// orderSchema.methods.calculateTotals = function () {
//   this.subtotal = this.items.reduce((sum, item) => sum + item.totalPrice, 0);
//   this.total = this.subtotal + this.tax + this.shipping;
//   return this;
// };

// // Pre-save middleware to ensure totals are calculated
// orderSchema.pre("save", function (next) {
//   // Calculate item total prices
//   this.items.forEach((item) => {
//     item.totalPrice = item.quantity * item.unitPrice;
//   });

//   // Calculate order totals
//   this.calculateTotals();

//   next();
// });

// // Static method to find orders by customer
// orderSchema.statics.findByCustomer = function (customerId) {
//   return this.find({ customerId }).sort({ createdAt: -1 });
// };

// // Static method to find orders by status
// orderSchema.statics.findByStatus = function (status) {
//   return this.find({ status }).sort({ createdAt: -1 });
// };

// // Instance method to update status
// orderSchema.methods.updateStatus = function (newStatus) {
//   this.status = newStatus;
//   return this.save();
// };

// // Instance method to cancel order
// orderSchema.methods.cancel = function () {
//   if (this.status === "pending" || this.status === "confirmed") {
//     this.status = "cancelled";
//     return this.save();
//   } else {
//     throw new Error(
//       "Order cannot be cancelled in current status: " + this.status
//     );
//   }
// };

// // Ensure virtual fields are serialized
// orderSchema.set("toJSON", {
//   virtuals: true,
// });

const Order = mongoose.model("Order", orderSchema);

module.exports = Order ;
