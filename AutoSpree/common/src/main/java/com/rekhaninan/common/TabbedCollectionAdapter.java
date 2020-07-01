package com.rekhaninan.common;

import android.os.Bundle;

import java.util.StringJoiner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import static com.rekhaninan.common.Constants.*;


public class TabbedCollectionAdapter extends FragmentStateAdapter {
    public String appName;
    private PlannerVwTabbed plannerVwTabbed;
    private MainVwTabbed mainVwTabbed;
    private ContactsVwTabbed contactsVwTabbed;
    private ShareVwTabbed shareVwTabbed;

    public TabbedCollectionAdapter(FragmentActivity frg)
    {

        super(frg);
    }



    private static final int MAIN_PAGE_TABS = 4;
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Log.d(getClass().getSimpleName(), "Creating fragment at=" + position);
        switch (position) {
            case  HOME_POSN: {
                mainVwTabbed = new MainVwTabbed();
                mainVwTabbed.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(MainVwTabbed.ARG_OBJECT, position + 1);
                mainVwTabbed.setArguments(args);
                return mainVwTabbed;
            }

            case PLANNER_POSN: {
                plannerVwTabbed  = new PlannerVwTabbed();
                plannerVwTabbed.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(PlannerVwTabbed.ARG_OBJECT, position + 1);
                plannerVwTabbed.setArguments(args);
                return plannerVwTabbed;
            }

            case CONTACTS_POSN: {
                contactsVwTabbed = new ContactsVwTabbed();
                contactsVwTabbed.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ContactsVwTabbed.ARG_OBJECT, position + 1);
                contactsVwTabbed.setArguments(args);
                return contactsVwTabbed;
            }

            case SHARE_POSN: {
                shareVwTabbed = new ShareVwTabbed();
                shareVwTabbed.app_name = appName;
                Bundle args = new Bundle();
                // Our object is just an integer :-P
                args.putInt(ShareVwTabbed.ARG_OBJECT, position + 1);
                shareVwTabbed.setArguments(args);
                return shareVwTabbed;
            }

            default:
                break;

        }
        Fragment fragment = new ShareVwTabbed();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ShareVwTabbed.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {

        return MAIN_PAGE_TABS;
    }

    public void refresh()
    {
        plannerVwTabbed.refresh();
    }

    public void refreshMainVw()
    {
        mainVwTabbed.refresh();
    }

    public void refreshContactVw() {contactsVwTabbed.refresh();}

    public void refreshShareVw() {shareVwTabbed.refresh();}

}
