package sallycs.com.pomoapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Adapt extends RecyclerView.Adapter<Adapt.ViewHolder>{

    List<Task> list;
    Context context;

    //Entire class creates an adapter for the HomeFragment to load the list of tasks and breaks

    public Adapt(List<Task> thingy, Context context){
        this.list = thingy;
        this.context = context;
    }
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_items, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Task thing = list.get(i);

        viewHolder.heading.setText(thing.getName());
        viewHolder.describe.setText(thing.getDescription());
        viewHolder.time.setText(thing.getTime() + ":00");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView heading;
        public TextView describe;
        public TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.textHead);
            describe = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time_view);
        }
    }
}
