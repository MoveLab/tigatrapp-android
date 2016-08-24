package ceab.movelab.tigabib.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ceab.movelab.tigabib.NotificationActivity;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.Util;
import ceab.movelab.tigabib.adapters.NotificationAdapter;
import ceab.movelab.tigabib.model.Notification;
import ceab.movelab.tigabib.model.RealmHelper;

/**
 * Created by eideam on 12/08/2016.
 */
public class FragmentList extends Fragment {
    //public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_DONE = "ARG_DONE";

    private boolean isDone;
    private ListView mListView;
    private NotificationAdapter mAdapter;
    private ArrayList<Notification> notifData = new ArrayList<>();

    public static FragmentList newInstance(boolean done) {
Log.d(ARG_DONE, "newInstance: isDone = " + done);

        Bundle args = new Bundle();
        args.putBoolean(ARG_DONE, done);
        FragmentList fragment = new FragmentList();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDone = (getArguments() != null ? getArguments().getBoolean(ARG_DONE) : false);
        Util.logInfo(getActivity(), ARG_DONE, "isDone = " + isDone);
        //isDone = getArguments().getBoolean(ARG_DONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mListView = (ListView) view.findViewById(R.id.listview);
        mAdapter = new NotificationAdapter(getActivity(), R.layout.notifications_item, notifData);
        mListView.setAdapter(mAdapter);

        mListView.setFastScrollEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//Toast.makeText(getApplicationContext(), String.valueOf(notifData.get(position).getId()), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity(), NotificationActivity.class);
                i.putExtra(NotificationActivity.NOTIFICATION_ID, notifData.get(position).getId());
                startActivity(i);
            }
        });

        loadNotifications(isDone);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void loadNotifications(boolean isDone) {
        notifData.clear();
        List<Notification> notificationList = RealmHelper.getInstance().getNotificationsRead(isDone);
        for (Notification notif : notificationList )
            notifData.add(notif);

        mAdapter.notifyDataSetChanged();
    }

}
