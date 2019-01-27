package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.databinding.NoteItemBinding;
import org.affordablehousing.chi.housingapp.model.Note;
import org.affordablehousing.chi.housingapp.ui.EditNoteMenuClickListener;
import org.affordablehousing.chi.housingapp.ui.NoteClickCallback;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter <NoteAdapter.NoteViewHolder> {

    private List <? extends Note> mNoteList;
    private NoteClickCallback mNoteClickCallback;
    private EditNoteMenuClickListener mEditNoteMenuClickListener;
    private Context mContext;

    public NoteAdapter(NoteClickCallback noteClickCallback, EditNoteMenuClickListener editNoteMenuClickListener, Context context) {
        this.mNoteClickCallback = noteClickCallback;
        this.mEditNoteMenuClickListener = editNoteMenuClickListener;
        this.mContext = context;
    }


    public void setNoteList(final List <? extends Note> notes) {
        if (mNoteList == null) {
            mNoteList = notes;
            notifyItemRangeInserted(0, notes.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mNoteList.size();
                }

                @Override
                public int getNewListSize() {
                    return notes.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    Note old = mNoteList.get(oldItemPosition);
                    Note note = notes.get(newItemPosition);
                    return old.getNoteId() == note.getNoteId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Note old = mNoteList.get(oldItemPosition);
                    Note note = notes.get(newItemPosition);
                    return old.getNoteId() == note.getNoteId()
                            && old.getPostedDate() == note.getPostedDate()
                            && Objects.equals(old.getText(), note.getText());
                }
            });
            mNoteList = notes;
            diffResult.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.note_item,
                        parent, false);
        binding.setCallback(mNoteClickCallback);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        holder.binding.setNote(mNoteList.get(position));

        int noteId = mNoteList.get(position).getNoteId();
        String noteText = mNoteList.get(position).getText();

        holder.binding.tvMenuNoteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.inflate(R.menu.note_edit);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_note_edit:
                                showEditNoteDialog(noteId, noteText);
                                return true;
                            case R.id.action_note_delete:
                                mEditNoteMenuClickListener.deleteNote(noteId);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        holder.binding.executePendingBindings();
    }

    private void showEditNoteDialog(int noteId, String noteText) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final View rowView = inflater.inflate(R.layout.edit_note_item, null);

        EditText noteTextView = rowView.findViewById(R.id.et_note_edit);

        noteTextView.setText(noteText, TextView.BufferType.EDITABLE);
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.edit_note));
        // add the buttons
        builder.setPositiveButton(mContext.getResources().getString(R.string.save_note), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mEditNoteMenuClickListener.editNote(noteId, noteTextView.getText().toString());
            }
        });
        builder.setNegativeButton(mContext.getResources().getString(R.string.cancel_note), null);
        builder.setView(rowView);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mNoteList == null ? 0 : mNoteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        final NoteItemBinding binding;

        NoteViewHolder(NoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
