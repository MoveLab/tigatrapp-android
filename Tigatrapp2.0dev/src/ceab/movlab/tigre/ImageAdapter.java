package ceab.movlab.tigre;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {

	private Activity activity;
	private static LayoutInflater inflater = null;
	public Integer[] data = { R.drawable.a, R.drawable.c, R.drawable.d,
			R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h,
			R.drawable.i, R.drawable.k, R.drawable.j, R.drawable.l, };

	public static final String[] captions = {
			"Femella de mosquit tigre. Observa la forma, el color i les característiques ratlles blanques.",
			"Femella de mosquit tigre. Observa les ratlles a l’abdomen. Observa també els plomalls. Són diferents als del mascle?",
			"Femella de mosquit tigre preparada per picar. Observa la forma, el color i les ratlles blanques.",
			"Femella de mosquit tigre picant. Observa la característica ratlla blanca al cap i al tòrax.",
			"Femella de mosquit tigre picant. Observa la característica ratlla blanca al cap i al tòrax.",
			"Dues femelles de mosquit tigre picant. La de l’esquerra ja té sang acumulada a l’abdomen, mentre que la de la dreta encara no.",
			"Femella de mosquit tigre picant. La sang que acumula a l'abdomen servirà per generar una posta d'ous.",
			"Posta d’ous de mosquit tigre. Observa el color fosc i la forma allargada. Els ous són tan petits que són quasi invisibles a ull nu.",
			"Larves (més allargades) i pupes (més arrodonides) de mosquit tigre. Observa que són aquàtiques. Les larves emergeixen dels ous i amb el temps fan la muda a pupes.",
			"Larves (més allargades) i pupes (més arrodonides) de mosquit tigre. Observa que són aquàtiques. Les larves emergeixen dels ous i amb el temps fan la muda a pupes.",
			"Dues pupes de mosquit tigre. En aquesta fase es produeix la metamorfosi, i el mosquit adult emergeix de la pupa cap al medi aeri." };

	public ImageAdapter(Activity a) {
		activity = a;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public ImageView image;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.image_gallery_items, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) vi.findViewById(R.id.image);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();
		final int stub_id = data[position];
		holder.image.setImageResource(stub_id);
		return vi;
	}

}
