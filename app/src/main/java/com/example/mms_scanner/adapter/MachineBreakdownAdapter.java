//package com.example.mms_scanner.adapter;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.mms_scanner.R;
//import com.example.mms_scanner.model.breakdown.machine_breckdown.MachineBreakdown;
//import com.example.mms_scanner.ui.BreakdownException_Fragment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MachineBreakdownAdapter extends RecyclerView.Adapter<MachineBreakdownAdapter.ViewHolder> implements Filterable{
//
//    Context context;
//    List<MachineBreakdown> machineBreakdownList;
//    List<MachineBreakdown> filteredProgramList;
//
//    int selectedItemPosition = -1;
//
//    public MachineBreakdownAdapter(Context context, List<MachineBreakdown> machineBreakdownList) {
//        this.context = context;
//        this.machineBreakdownList = machineBreakdownList;
//        this.filteredProgramList = machineBreakdownList;
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charSequenceString = constraint.toString();
//                if (charSequenceString.isEmpty()) {
//                    filteredProgramList = machineBreakdownList;
//                } else {
//                    List<MachineBreakdown> filteredList = new ArrayList<>();
//                    for (MachineBreakdown programNumber : machineBreakdownList) {
//                        if (programNumber.getMachineCode().toLowerCase().contains(charSequenceString.toLowerCase())) {
//                            filteredList.add(programNumber);
//                        }
//                        filteredProgramList = filteredList;
//                    }
//                }
//                FilterResults results = new FilterResults();
//                results.values = filteredProgramList;
//                Log.i("info","results: "+results.values);
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filteredProgramList = (List<MachineBreakdown>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//
//    @NonNull
//    @Override
//    public MachineBreakdownAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.machine_breakdown_recycalerview, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MachineBreakdownAdapter.ViewHolder holder, int position) {
//        final MachineBreakdown machineBreakdown = filteredProgramList.get(position);
//        holder.machineCodeTxt.setText(machineBreakdown.getMachineCode());
//    }
//
//    @Override
//    public int getItemCount() {
//        return filteredProgramList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        CardView itemLayout;
//        TextView machineCodeTxt,StartTimeTxt,EndTimeTxt,Time_CountTxt;
//        CheckBox confirmChBx;
//        Button btnApprove;
//        ImageView imgDetails;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            itemLayout = itemView.findViewById(R.id.itemLayout);
//            machineCodeTxt = itemView.findViewById(R.id.machineCodeTxt);
//            StartTimeTxt = itemView.findViewById(R.id.StartTimeTxt);
//            EndTimeTxt = itemView.findViewById(R.id.EndTimeTxt);
//            Time_CountTxt = itemView.findViewById(R.id.Time_CountTxt);
//            confirmChBx = itemView.findViewById(R.id.confirmChBx);
//            btnApprove = itemView.findViewById(R.id.btnApprove);
//            imgDetails = itemView.findViewById(R.id.imgDetails);
//        }
//    }
//}
