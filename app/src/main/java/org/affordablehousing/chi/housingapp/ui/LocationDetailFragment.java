package org.affordablehousing.chi.housingapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.NoteAdapter;
import org.affordablehousing.chi.housingapp.databinding.FragmentLocationDetailBinding;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.model.Note;
import org.affordablehousing.chi.housingapp.model.NoteEntity;
import org.affordablehousing.chi.housingapp.viewmodel.LocationViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class LocationDetailFragment extends Fragment {

    private static final String KEY_LOCATION_ID = "location-id";
    private int LOCATION_ID;
    private FragmentLocationDetailBinding mBinding;
    private LocationViewModel mLocationViewModel;
    private NoteAdapter mNoteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_detail, container, false);

        Button btnAddNote = mBinding.getRoot().findViewById(R.id.btn_add_note);
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNoteDialogButtonClicked(inflater);
            }
        });

        LOCATION_ID = getArguments().getInt(KEY_LOCATION_ID);

        LocationViewModel.Factory factory = new LocationViewModel.Factory(
                getActivity().getApplication(), LOCATION_ID);

        mLocationViewModel =
                ViewModelProviders.of(this, factory)
                        .get(LocationViewModel.class);

        mNoteAdapter = new NoteAdapter(mNoteClickCallback, mEditNoteMenuClickListener, getContext());
        mBinding.noteList.setAdapter(mNoteAdapter);

        return mBinding.getRoot();
    }

    public void showAddNoteDialogButtonClicked(LayoutInflater inflater) {

        final View rowView = inflater.inflate(R.layout.edit_note_item, null);

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add a Note");

        // add the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // EditText noteView = builder.getApplicationContext().findViewById(R.id.et_note_edit);
                EditText noteView = rowView.findViewById(R.id.et_note_edit);
                String noteText = noteView.getText().toString();
                NoteEntity newNote = new NoteEntity();
                newNote.setLocationId(LOCATION_ID);
                newNote.setText(noteText);
                mLocationViewModel.addNote(newNote);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.setView(rowView);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        LocationViewModel.Factory factory = new LocationViewModel.Factory(
                getActivity().getApplication(), getArguments().getInt(KEY_LOCATION_ID));

        final LocationViewModel locationViewModel =
                ViewModelProviders.of(this, factory)
                        .get(LocationViewModel.class);

        ToggleButton fav = mBinding.getRoot().findViewById(R.id.tb_favorite);

        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if ( isChecked ) {
                    fav.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_star_24px));
                    Toast toast = Toast.makeText(getContext(),
                            locationViewModel.getObservableLocation().getValue().getProperty_name() + " added to favorites.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                   fav.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_star_border_24px));
                    Toast toast = Toast.makeText(getContext(),
                            locationViewModel.getObservableLocation().getValue().getProperty_name() + " removed from favorites.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                int locationId = locationViewModel.getObservableLocation().getValue().getLocationId();
                locationViewModel.setFavorite(locationId, isChecked);
            }
        });


        mBinding.setLocationViewModel(locationViewModel);

        subscribeToModel(locationViewModel);

    }

    private void subscribeToModel(final LocationViewModel model) {

        // Observe product data
        model.getObservableLocation().observe(this, new Observer <LocationEntity>() {
            @Override
            public void onChanged(@Nullable LocationEntity locationEntity) {
                model.setLocation(locationEntity);
            }
        });


        // Observe notes
        model.getNotes().observe(this, new Observer <List <NoteEntity>>() {
            @Override
            public void onChanged(@Nullable List <NoteEntity> noteEntities) {
                if (noteEntities != null) {
                    mBinding.setIsLoading(false);
                    mNoteAdapter.setNoteList(noteEntities);
                } else {
                    mBinding.setIsLoading(true);
                }
            }
        });
    }

    private final NoteClickCallback mNoteClickCallback = new NoteClickCallback() {

        @Override
        public void onClick(Note note) {
//            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//               ((MapsActivity) getActivity()).showEditNoteMenu(note);
//            }
        }

    };

    private final EditNoteMenuClickListener mEditNoteMenuClickListener = new EditNoteMenuClickListener() {
        @Override
        public void editNote(int noteId, String noteText) {
            mLocationViewModel.editNote(noteId, noteText);
        }

        @Override
        public void deleteNote(int noteId) {
            mLocationViewModel.deleteNote(noteId);
        }
    };

    /**
     * Creates location fragment for specific product ID
     */
    public static LocationDetailFragment forLocation(int locationId) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LOCATION_ID, locationId);
        fragment.setArguments(args);
        return fragment;
    }
}