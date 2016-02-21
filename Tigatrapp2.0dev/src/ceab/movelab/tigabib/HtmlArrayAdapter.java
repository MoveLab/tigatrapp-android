package ceab.movelab.tigabib;

import android.content.Context;
import android.text.Html;
import android.widget.ArrayAdapter;

public class HtmlArrayAdapter extends ArrayAdapter {

	public HtmlArrayAdapter(Context context, int resource, Object[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
    public Object getItem(int position){
        return Html.fromHtml(this.arr_sort.get(position));
    }
	
}
