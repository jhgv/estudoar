package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class VerDoacaoFragment extends Fragment implements View.OnClickListener{

    private TextView nome;
    private TextView categoria;
    private TextView assunto;
    private TextView descricao;

    private ImageView foto;

    private Button queroBtn;
    private Button contatoBtn;

    private ParseUser currentUser;
    private ParseObject doacaoAtual;

    private String id_doador;

    boolean minhaDoacao = false;

    private int mId = 1;

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

        queroBtn = (Button) doacaoView.findViewById(R.id.btnQuero);

        contatoBtn = (Button) doacaoView.findViewById(R.id.btnContatoDoador);

        Intent intent = getActivity().getIntent();

        id_doador = intent.getExtras().getString("id_doador");

        if (id_doador.equals(currentUser.getObjectId())){
            contatoBtn.setText("Editar Doação");
            queroBtn.setVisibility(View.GONE);
            minhaDoacao = true;
        }

        String id_doacao = intent.getExtras().getString("id_doacao");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.getInBackground(id_doacao, new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    doacaoAtual = doacao;
                    nome.setText(doacaoAtual.getString("nome"));
                    categoria.setText(doacaoAtual.getString("categoria"));
                    assunto.setText(doacaoAtual.getString("assunto"));
                    descricao.setText(doacaoAtual.getString("descricao"));

                    final ParseFile image_file = (ParseFile) doacaoAtual.getParseFile("foto");

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

                    if(!minhaDoacao) {
                        ParseQuery<ParseObject> queryFavorito = ParseQuery.getQuery("Favoritos");
                        queryFavorito.whereEqualTo("interessado", currentUser.getObjectId());
                        queryFavorito.whereEqualTo("doacao", doacaoAtual.getObjectId());
                        queryFavorito.getFirstInBackground(new GetCallback<ParseObject>() {
                            public void done(ParseObject favorito, ParseException e) {
                                if (favorito != null) {
                                    queroBtn.setText("Não Quero");
                                } else {
                                    //Toast.makeText(getActivity(), "Erro ao favoritar!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(getActivity(), "ID Desconhecido!", Toast.LENGTH_LONG).show();
                }
            }
        });

        queroBtn.setOnClickListener(this);
        contatoBtn.setOnClickListener(this);

        return doacaoView;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnQuero:
                marcarInteresse(v);
                break;
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

    public void marcarInteresse(View view){
        ParseQuery<ParseObject> queryFavorito = ParseQuery.getQuery("Favoritos");

        queryFavorito.whereEqualTo("interessado", currentUser.getObjectId());
        queryFavorito.whereEqualTo("doacao", doacaoAtual.getObjectId());

        queryFavorito.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> favoritos, ParseException e) {
                if (favoritos.size() != 0){
                    ParseObject.deleteAllInBackground(favoritos);
                    queroBtn.setText("Quero");
                    Toast.makeText(getActivity(), "Deletado o favorito", Toast.LENGTH_SHORT).show();

                } else {
                    ParseObject favorito = new ParseObject("Favoritos");

                    favorito.put("interessado", currentUser.getObjectId());
                    favorito.put("doacao", doacaoAtual.getObjectId());

                    favorito.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                queroBtn.setText("Não Quero");
                                Toast.makeText(getActivity(), "Doação Salva", Toast.LENGTH_SHORT).show();

                                ParseQuery queryNotify = ParseInstallation.getQuery();
                                //queryNotify.whereEqualTo("objectId",currentUser.getObjectId()); //id_doador
                                JSONObject data = null;

                                try {
                                    data = new JSONObject();
                                    data.put("doador_id", id_doador);
                                    data.put("interessado_id", currentUser.getObjectId());
                                    data.put("interessado_name",currentUser.getUsername());
                                } catch(JSONException ej) {
                                    ej.printStackTrace();
                                }

                                ParsePush androidPush = new ParsePush();
                                androidPush.setData(data);
                                androidPush.setQuery(queryNotify);
                                androidPush.sendInBackground();

                            } else {
                                Toast.makeText(getActivity(), "Erro ao salvar a doação", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

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
