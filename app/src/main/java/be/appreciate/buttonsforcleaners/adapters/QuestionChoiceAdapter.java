package be.appreciate.buttonsforcleaners.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.model.QuestionType;

/**
 * Created by Inneke De Clippel on 14/03/2016.
 */
public class QuestionChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private boolean singleChoice;
    private List<String> answers;
    private List<String> selectedAnswers;

    private static final int TYPE_SINGLE_CHOICE = 1;
    private static final int TYPE_MULTI_CHOICE = 2;

    public QuestionChoiceAdapter(QuestionType questionType)
    {
        this.singleChoice = questionType == null || questionType != QuestionType.MULTI_CHOICE;
        this.selectedAnswers = new ArrayList<>();
    }

    public void setAnswers(List<String> answers)
    {
        this.answers = answers;
        this.notifyDataSetChanged();
    }

    public void setSelectedAnswers(List<String> selectedAnswers)
    {
        this.selectedAnswers = selectedAnswers != null ? new ArrayList<>(selectedAnswers) : new ArrayList<>();
        this.notifyDataSetChanged();
    }

    public List<String> getSelectedAnswers()
    {
        return this.selectedAnswers;
    }

    private void addSelectedAnswer(String answer)
    {
        if(!this.selectedAnswers.contains(answer))
        {
            if(this.singleChoice && this.selectedAnswers.size() > 0)
            {
                String previousAnswer = this.selectedAnswers.get(0);
                int previousAnswerPosition = this.answers != null ? this.answers.indexOf(previousAnswer) : -1;
                this.selectedAnswers.remove(previousAnswer);

                if(previousAnswerPosition >= 0)
                {
                    this.notifyItemChanged(previousAnswerPosition);
                }
            }

            int newAnswerPosition = this.answers != null ? this.answers.indexOf(answer) : -1;
            this.selectedAnswers.add(answer);

            if(newAnswerPosition >= 0)
            {
                this.notifyItemChanged(newAnswerPosition);
            }
        }
    }

    private void removeSelectedAnswer(String answer)
    {
        if(this.selectedAnswers.contains(answer) && !this.singleChoice)
        {
            int answerPosition = this.answers != null ? this.answers.indexOf(answer) : -1;
            this.selectedAnswers.remove(answer);

            if(answerPosition >= 0)
            {
                this.notifyItemChanged(answerPosition);
            }
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return this.singleChoice ? TYPE_SINGLE_CHOICE : TYPE_MULTI_CHOICE;
    }

    @Override
    public int getItemCount()
    {
        return this.answers != null ? this.answers.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_MULTI_CHOICE:
                View viewMultiChoice = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_question_multi_choice, parent, false);
                return new AnswerViewHolder(viewMultiChoice);

            case TYPE_SINGLE_CHOICE:
                View viewSingleChoice = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_question_single_choice, parent, false);
                return new AnswerViewHolder(viewSingleChoice);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof AnswerViewHolder)
        {
            String answer = this.answers.get(position);
            boolean selected = this.selectedAnswers.contains(answer);
            ((AnswerViewHolder) holder).bind(answer, selected);
        }
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private CompoundButton control;
        private TextView textViewAnswer;
        private String answer;
        private boolean selected;

        public AnswerViewHolder(View itemView)
        {
            super(itemView);

            this.control = (CompoundButton) itemView.findViewById(R.id.control);
            this.textViewAnswer = (TextView) itemView.findViewById(R.id.textView_answer);

            itemView.setOnClickListener(this);
        }

        public void bind(String answer, boolean selected)
        {
            this.answer = answer;
            this.selected = selected;

            this.control.setChecked(selected);
            this.textViewAnswer.setText(answer);
        }

        @Override
        public void onClick(View v)
        {
            if(this.selected)
            {
                QuestionChoiceAdapter.this.removeSelectedAnswer(this.answer);
            }
            else
            {
                QuestionChoiceAdapter.this.addSelectedAnswer(this.answer);
            }
        }
    }
}
