package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class VerDoacaoFragment extends Fragment implements View.OnClickListener{

    private TextView nome;
    private TextView categoria;
    private TextView assunto;
    private TextView descricao;

    private ImageView foto;

    private Button contatoBtn;

    private ParseUser currentUser;
    boolean minhaDoacao = false;

    public VerDoacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
        }

        View doacaoView = inflater.inflate(R.layout.fragment_ver_doacao,container,false);

        currentUser = ParseUser.getCurrentUser();

        nome = (TextView) doacaoView.findViewById(R.id.nome_doacao);
        categoria = (TextView) doacaoView.findViewById(R.id.categoria_doacao);
        assunto = (TextView) doacaoView.findViewById(R.id.assunto_doacao);
        descricao = (TextView) doacaoView.findViewById(R.id.descricao_doacao);

        foto = (ImageView) doacaoView.findViewById(R.id.foto_doacao);

        contatoBtn = (Button) doacaoView.findViewById(R.id.btnContatoDoador);

        Intent intent = getActivity().getIntent();

        String id_doador = intent.getExtras().getString("id_doador");

        if (id_doador.equals(currentUser.getObjectId())){
            contatoBtn.setText("Editar Doação");
            minhaDoacao = true;
        }

        String id_doacao = intent.getExtras().getString("id_doacao");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.getInBackground(id_doacao, new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    nome.setText(doacao.getString("nome"));
                    categoria.setText(doacao.getString("categoria"));
                    assunto.setText(doacao.getString("assunto"));
                    descricao.setText(doacao.getString("descricao"));

                    final ParseFile image_file = (ParseFile) doacao.getParseFile("foto");

                    new Thread(new Runnable() {
                        public void run() {
                            foto.post(new Runnable() {
                                public void run() {
                                    if (image_file != null) {
                                        image_file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null) {
                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);

                                                    if (bmp != null) foto.setImageBitmap(bmp);
                                                }
                                            }
                                        });
                                    } else {
                                        foto.setPadding(10, 10, 10, 10);
                                    }
                                }
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(getActivity(), "ID Desconhecido!", Toast.LENGTH_LONG).show();
                }
            }
        });

        contatoBtn.setOnClickListener(this);

        return doacaoView;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnContatoDoador:
                if(minhaDoacao)
                    goToEditarDoacao(v);
                else
                    goToContatoDoador(v);

                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void goToContatoDoador(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new ContatoDoadorFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void goToEditarDoacao(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new EditarDoacaoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
