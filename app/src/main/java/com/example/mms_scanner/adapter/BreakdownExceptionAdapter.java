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
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.mms_scanner.R;
//import com.example.mms_scanner.model.breakdown.machine_breakdown_exception.BreakdownException;
//import com.example.mms_scanner.model.breakdown.machine_breckdown.MachineBreakdown;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BreakdownExceptionAdapter extends RecyclerView.Adapter<BreakdownExceptionAdapter.ViewHolder> implements Filterable {
//
//    Context context;
//    List<BreakdownException> breakdownExceptionList;
//    List<BreakdownException> filteredProgramList;
//
//    public BreakdownExceptionAdapter(Context context, List<BreakdownException> breakdownExceptionList) {
//        this.context = context;
//        this.breakdownExceptionList = breakdownExceptionList;
//        this.filteredProgramList = breakdownExceptionList;
//    }
//
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                String charSequenceString = constraint.toString();
//                if (charSequenceString.isEmpty()) {
//                    filteredProgramList = breakdownExceptionList;
//                } else {
//                    List<BreakdownException> filteredList = new ArrayList<>();
//                    for (BreakdownException programNumber : breakdownExceptionList) {
//                        if (programNumber.getReasonName().toLowerCase().contains(charSequenceString.toLowerCase())) {
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
//                filteredProgramList = (List<BreakdownException>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//
//    @NonNull
//    @Override
//    public BreakdownExceptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.breakdown_exception_recyclerview, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BreakdownExceptionAdapter.ViewHolder holder, int position) {
//        final BreakdownException breakdownException = filteredProgramList.get(position);
//        holder.reasonTxt.setText(breakdownException.getReasonName());
//    }
//
//    @Override
//    public int getItemCount() {
//        return filteredProgramList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        CardView exceptionLayout;
//        CheckBox item_check;
//        TextView reasonTxt;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            exceptionLayout = itemView.findViewById(R.id.exceptionLayout);
//            item_check = itemView.findViewById(R.id.item_check);
//            reasonTxt = itemView.findViewById(R.id.reasonTxt);
//        }
//    }
//}
