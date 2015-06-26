package estudoar.cin.ufpe.br.estudoar;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class EditarPerfilFragment extends Fragment implements View.OnClickListener {

    private Button editarBtn;

    private EditText nome;
    private EditText email;
    private TextView telefone;

    private ParseUser currentUser;

    public EditarPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View editarPerfilView = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        if(savedInstanceState == null){
        }

        currentUser = ParseUser.getCurrentUser();

        nome = (EditText) editarPerfilView.findViewById(R.id.nome_editar);
        email = (EditText) editarPerfilView.findViewById(R.id.email_editar);
        telefone = (TextView) editarPerfilView.findViewById(R.id.telefone_editar);

        editarBtn = (Button) editarPerfilView.findViewById(R.id.btnEditar);

        nome.setText(currentUser.getString("name"));
        email.setText(currentUser.getEmail());
        telefone.setText(currentUser.getString("telefone"));

        editarBtn.setOnClickListener(this);

        return editarPerfilView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnEditar:
                editar(v);
                break;

        }
    }

    public void editar(View v) {

        String new_name = nome.getText().toString().trim();
        String new_email = email.getText().toString().trim();
        String new_telefone = telefone.toString().trim();

        currentUser.put("name", new_name);
        currentUser.setEmail(new_email);
        currentUser.put("telefone",new_telefone);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "As modificações foram salvas com sucesso", Toast.LENGTH_LONG).show();
                    goToMeuPerfil();
                } else {
                    Toast.makeText(getActivity(), "Erro ao Editar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToMeuPerfil(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_meu_perfil, new MeuPerfilFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
