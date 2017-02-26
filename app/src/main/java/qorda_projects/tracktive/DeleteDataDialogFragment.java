package qorda_projects.tracktive;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import qorda_projects.tracktive.data.CardsContract;

/**
 * Created by sorengoard on 25/02/2017.
 */

public class DeleteDataDialogFragment extends DialogFragment {

    private final String DELETE_TAG = "delete_cards_dialog";
    private final String DELETE_STRING = "delete_all";

    public interface deleteDialogListener {

        void recreateActivityAfterDeleteCall();
    }

    private deleteDialogListener mDeleteListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //verify that host activity implements the callback interface
        try {
            //instantiate the listener so that we can send events to the host
            mDeleteListener = (deleteDialogListener) activity;
        } catch (ClassCastException e) {
            // if this happens the activity doesn't implement the interface so throw an exception
            throw new ClassCastException(activity.toString() + " must implement deleteDialogListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().getContentResolver().delete(CardsContract.CardEntry.CONTENT_URI, null, null);
                        // clear sharedPrefs
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear().commit();
                        mDeleteListener.recreateActivityAfterDeleteCall();

                    }
                })
            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
        return builder.create();
    }
}
