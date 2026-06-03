package com.example.coffeeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderDate.setText(order.date);
        holder.orderTotal.setText("KES " + order.totalPrice);
        holder.orderCustomer.setText("Customer: " + order.customerName + " (" + order.username + ")");
        holder.paymentMethod.setText("Payment: " + order.paymentMethod);

        StringBuilder details = new StringBuilder();
        int coffeePrice = order.coffeeQuantity * 50; // Using 50 as coffee price
        details.append("Coffee x").append(order.coffeeQuantity).append(" (KES ").append(coffeePrice).append(")");
        
        if (order.toppings != null && !order.toppings.isEmpty()) {
            for (Map.Entry<String, Integer> entry : order.toppings.entrySet()) {
                String toppingName = entry.getKey();
                int toppingQty = entry.getValue();
                int toppingPrice = 0;
                
                // Matching prices from MainActivity
                switch (toppingName) {
                    case "Chapati": toppingPrice = toppingQty * 20; break;
                    case "Mandazi": toppingPrice = toppingQty * 10; break;
                    case "Githeri": toppingPrice = toppingQty * 30; break;
                    case "Bread": toppingPrice = toppingQty * 35; break;
                }
                
                if (toppingQty > 0) {
                    details.append("\n").append(toppingName).append(" x").append(toppingQty)
                           .append(" (KES ").append(toppingPrice).append(")");
                }
            }
        }
        holder.orderDetails.setText(details.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderDate, orderTotal, orderCustomer, orderDetails, paymentMethod;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderCustomer = itemView.findViewById(R.id.orderCustomer);
            orderDetails = itemView.findViewById(R.id.orderDetails);
            paymentMethod = itemView.findViewById(R.id.paymentMethod);
        }
    }
}
