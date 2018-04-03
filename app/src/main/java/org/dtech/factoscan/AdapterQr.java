package org.dtech.factoscan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by lenovo on 31/03/2018.
 */

public class AdapterQr extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context _context;
    private LayoutInflater inflater;
    List<String> scanres = Collections.emptyList();

    public AdapterQr(Context context, List<String> scanres) {
        this._context = context;
        this.scanres = scanres;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tes_layout, parent, false);
        MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        //DataQr current = scanres.get(position);
        myHolder.tscan.setText(scanres.get(position));
        myHolder.bDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanres.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, scanres.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanres.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tscan;
        ImageButton bDel;

        public MyHolder(View itemView) {
            super(itemView);
            tscan = itemView.findViewById(R.id.tt);
            bDel = itemView.findViewById(R.id.imgDel);
            //bDel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //scanres.remove()
        }
    }
}
