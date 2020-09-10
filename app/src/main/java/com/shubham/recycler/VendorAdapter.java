package com.shubham.recycler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VendorAdapter extends FirestoreRecyclerAdapter<VendorLIst, VendorAdapter.VendorHolder> {

    private Context mContext;
    private List<VendorLIst> mUploads;
    LinearLayout linearLayout;
    private String shopName,shopEmail,shopPhone,shopAddress,shopProfileLink,vendorId;
    int iPosition;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public VendorAdapter(@NonNull FirestoreRecyclerOptions<VendorLIst> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final VendorHolder holder, int position, @NonNull VendorLIst model) {
        Upload uploadCurrent = new Upload(model.getName(),model.getProfile());

        holder.name.setText(model.getName());
        holder.address.setText(model.getAddress());

        Picasso.with(mContext)
                .load(uploadCurrent.getmImageUrl()).fit()
                .into(holder.profile);
        try {
            holder.singleCardUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iPosition = holder.getAdapterPosition();
                    shopName = getItem(iPosition).getName();
                    Log.d("testName",shopName+" position="+iPosition);
                    shopAddress = getItem(iPosition).getAddress();
                    shopEmail = getItem(iPosition).getEmail();
                    shopPhone = getItem(iPosition).getPhone();
                    shopProfileLink = getItem(iPosition).getProfile();
                    vendorId = getItem(iPosition).getUserId();

                    Intent intent = new Intent(view.getContext(),Order.class);
                    Log.d("testInner", shopName);
                    intent.putExtra("name",shopName);
                    intent.putExtra("email",shopEmail);
                    intent.putExtra("address",shopAddress);
                    intent.putExtra("phone",shopPhone);
                    intent.putExtra("link",shopProfileLink);
                    intent.putExtra("vendorId",vendorId);
                    Log.d("vendorId",vendorId);
                    view.getContext().startActivity(intent);
                }
            });
        }catch (Exception e){
            Toast.makeText(mContext,""+e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }


        //Glide.with(mContext).load(uploadCurrent.getmImageUrl()).asBitmap().into(holder.profile);
        //holder.profile.setImageURI(Uri.parse(model.getProfile()));
        //Picasso.with(getBaseContext()).load(imageUrl).into(imgFirebase);
        //String imageUrl = ProfileUploadVendor.getImageUrl();
        //holder.profile.setImageURI(Uri.parse(model.getProfile()));
        //Picasso.with(mContext).load(Uri.parse(imageUrl)).into(holder.profile);
    }

    @NonNull
    @Override
    public VendorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_vendor_row,parent,false);
        return new VendorHolder(v);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    class VendorHolder extends RecyclerView.ViewHolder{

        TextView name,address;
        ImageView profile;
        LinearLayout linearLayout;
        RecyclerView recyclerView;
        CardView singleCardUser;
        public VendorHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            profile = itemView.findViewById(R.id.profile);
            linearLayout = itemView.findViewById(R.id.vendorList);
            recyclerView = itemView.findViewById(R.id.firestoreList);
            singleCardUser = itemView.findViewById(R.id.singleUserCard);
        }
    }
}
