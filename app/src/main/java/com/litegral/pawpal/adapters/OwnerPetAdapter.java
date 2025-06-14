package com.litegral.pawpal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.litegral.pawpal.R;
import com.litegral.pawpal.activities.PetAdoptionRequestsActivity;
import com.litegral.pawpal.models.Pet;

import java.util.List;

public class OwnerPetAdapter extends RecyclerView.Adapter<OwnerPetAdapter.ViewHolder> {

    private final Context context;
    private final List<Pet> petList;
    private final FirebaseFirestore firestore;

    public OwnerPetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_owner_pet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.petName.setText(pet.getName());
        Glide.with(context).load(pet.getPhotoUrl()).centerCrop().into(holder.petImage);

        // Fetch and display the number of pending requests
        firestore.collection("adoptionRequests")
                .whereEqualTo("petId", pet.getId())
                .whereEqualTo("requestStatus", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int pendingCount = queryDocumentSnapshots.size();
                    holder.requestCount.setText(pendingCount + " pending request(s)");
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PetAdoptionRequestsActivity.class);
            intent.putExtra("petId", pet.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView petImage;
        TextView petName, requestCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.image_pet);
            petName = itemView.findViewById(R.id.text_pet_name);
            requestCount = itemView.findViewById(R.id.text_request_count);
        }
    }
}