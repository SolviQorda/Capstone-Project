package qorda_projects.tracktive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * Created by sorengoard on 25/01/2017.
 */

public class DiaryEntryDialog extends DialogFragment  {

    public interface diaryDialogListener {

        public void titleDiaryEntry();
        public void addDiaryBody();

    }
    // use this instance of interface to deliver action events
    diaryDialogListener mListener;

    //Override onAttach to instantiate the Listener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //verify that host activity implements the callback interface
        try {
            //instantiate the listender so that we can send events to the host
            mListener = (diaryDialogListener) activity;
        } catch (ClassCastException e) {
            // if this happens the activity doesn't implement the interface so throw an exception
            throw new ClassCastException(activity.toString() + " must implement diaryDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_diaryentry, null))
                .setPositiveButton(R.string.save_diary_entry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //`TODO: change sharedPrefs, make sure there is a listener for any sharedPreferenceChange to syncImmediately.
                        //TODO: write text to database. http://stackoverflow.com/questions/18685296/how-to-get-string-from-edittext-and-insert-it-to-sqlite-in-android

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
        return builder.create();
    }

}
