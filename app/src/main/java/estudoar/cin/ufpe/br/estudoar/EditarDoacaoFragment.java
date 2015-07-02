package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class EditarDoacaoFragment extends Fragment implements View.OnClickListener{

    private final int GET_PICTURE_CODE = 1;
    private final int GET_LOCATION_CODE = 2;
    public static final int GET_FROM_GALLERY = 3;

    private EditText nomeImput;
    private EditText assuntoImput;
    private EditText descricaoImput;

    private Button fotoGaleiraBtn;
    private Button fotoCameraBtn;
    private Button editarDoacaoBtn;
    private Button editarEnderecoBtn;

    private LinearLayout enderecoCheck;

    private ParseGeoPoint localizacao;
    private ParseUser currentUser;
    private ParseObject doacao_editada;

    private Spinner categorias_spinner;
    private String categoriaSelecionada;

    private ImageView fotoView;

    public EditarDoacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
        }

        View editarDoacaoView = inflater.inflate(R.layout.fragment_editar_doacao,container,false);

        currentUser = ParseUser.getCurrentUser();

        nomeImput = (EditText) editarDoacaoView.findViewById(R.id.nome_doacao_editar);
        assuntoImput = (EditText) editarDoacaoView.findViewById(R.id.assunto_doacao_editar);
        descricaoImput = (EditText) editarDoacaoView.findViewById(R.id.descricao_doacao_editar);

        fotoView = (ImageView) editarDoacaoView.findViewById(R.id.fotoViewEditar);

        categorias_spinner = (Spinner) editarDoacaoView.findViewById(R.id.spinner_doacao_editar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.categorias_array,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorias_spinner.setAdapter(adapter);
        categorias_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
                categoriaSelecionada = (String) categorias_spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView parentView) {

            }
        });

        fotoGaleiraBtn = (Button) editarDoacaoView.findViewById(R.id.btnFotoGaleriaEditar);
        fotoCameraBtn = (Button) editarDoacaoView.findViewById(R.id.btnFotoCameraEditar);
        editarDoacaoBtn = (Button) editarDoacaoView.findViewById(R.id.btnEditarDoacao);
        editarEnderecoBtn = (Button) editarDoacaoView.findViewById(R.id.btnEditAddress);

        enderecoCheck = (LinearLayout) editarDoacaoView.findViewById(R.id.editarEnderecoCheck);

        Intent intent = getActivity().getIntent();

        String id_doacao = intent.getExtras().getString("id_doacao");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.getInBackground(id_doacao, new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    doacao_editada = doacao;

                    final ParseFile image_file = (ParseFile) doacao.getParseFile("foto");

                    new Thread(new Runnable() {
                        public void run() {
                            fotoView.post(new Runnable() {
                                public void run() {
                                    if (image_file != null) {
                                        image_file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null) {
                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);
                                                    if (bmp != null) fotoView.setImageBitmap(bmp);
                                                }
                                            }
                                        });
                                    } else
                                        fotoView.setPadding(15, 15, 15, 15);
                                }
                            });
                        }
                    }).start();

                    nomeImput.setText(doacao.getString("nome"));

                    String categoria_antiga = doacao.getString("categoria");
                    ArrayAdapter myAdap = (ArrayAdapter) categorias_spinner.getAdapter();
                    int spinnerPosition = myAdap.getPosition(categoria_antiga);
                    categorias_spinner.setSelection(spinnerPosition);

                    assuntoImput.setText(doacao.getString("assunto"));
                    descricaoImput.setText(doacao.getString("descricao"));

                    localizacao = doacao.getParseGeoPoint("localizacao");
                    if(localizacao != null){
                        enderecoCheck.setVisibility(View.VISIBLE);
                        editarEnderecoBtn.setText("Remover Endereço");
                    }

                } else Toast.makeText(getActivity(), "ID Desconhecido!", Toast.LENGTH_LONG).show();
            }
        });

        fotoGaleiraBtn.setOnClickListener(this);
        fotoCameraBtn.setOnClickListener(this);
        editarDoacaoBtn.setOnClickListener(this);
        editarEnderecoBtn.setOnClickListener(this);

        return editarDoacaoView;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnEditarDoacao:
                salvarMudancas();
                break;

            case R.id.btnFotoGaleriaEditar:
                uploadFotoGaleria();
                break;
            case R.id.btnFotoCameraEditar:
                uploadFotoCamera();
                break;
            case R.id.btnEditAddress:
                openMaps(v);
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

    public void salvarMudancas(){
        final Dialog progressDialog = ProgressDialog.show(getActivity(), "", "Salvando...", true);

        String nome = nomeImput.getText().toString().trim();
        String assunto = assuntoImput.getText().toString().trim();
        String descricao = descricaoImput.getText().toString().trim();
        Bitmap foto = ((BitmapDrawable)fotoView.getDrawable()).getBitmap();

        doacao_editada.put("nome", nome);
        doacao_editada.put("categoria", categoriaSelecionada);
        doacao_editada.put("assunto", assunto);
        doacao_editada.put("descricao", descricao);

        if (foto == null){
            doacao_editada.put("foto",null);
        }else{
            // Convert it to byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            foto.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] image = stream.toByteArray();

            // Create the ParseFile
            String fileName = currentUser.getObjectId() + "_" + (int) System.currentTimeMillis() + ".jpg";
            ParseFile file = new ParseFile(fileName, image);
            // Upload the image into Parse Cloud
            file.saveInBackground();

            doacao_editada.put("foto", file);
        }

        if(localizacao != null){
            doacao_editada.put("localizacao", localizacao);
        }

        doacao_editada.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(getActivity(), "Doação Modificada", Toast.LENGTH_SHORT).show();
                    goToVerDoacao();
                } else {
                    Toast.makeText(getActivity(), "Erro ao salvar alterações", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void uploadFotoGaleria() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

    }

    public void uploadFotoCamera() {
        startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 1);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if( resultCode == Activity.RESULT_OK){
            if ((requestCode == GET_FROM_GALLERY)) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    fotoView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
           } else if (requestCode == GET_PICTURE_CODE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                fotoView.setImageBitmap(bitmap);
            }else if (requestCode == GET_LOCATION_CODE){
                if(localizacao == null){
                    //double[] result = data.getDoubleArrayExtra("position");
                    ArrayList<Double> coordenadas = (ArrayList<Double>) data.getSerializableExtra("coordenadas");
                    localizacao = new ParseGeoPoint(coordenadas.get(0), coordenadas.get(1));
                    enderecoCheck.setVisibility(View.VISIBLE);
                    editarEnderecoBtn.setText("Remover Endereço");
                }
            }
        }

        fotoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.preview_image);
                Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
                ivPreview.setImageDrawable(fotoView.getDrawable());
                ivPreview.setBackgroundColor(Color.BLACK);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        nagDialog.dismiss();
                    }
                });
                nagDialog.show();
            }
        });

    }

    public void openMaps(View v) {
        if(localizacao != null) {
            localizacao = null;
            Toast.makeText(getActivity(), "Endereço Removido!", Toast.LENGTH_SHORT).show();
            editarEnderecoBtn.setText("Adicionar Endereço");
            enderecoCheck.setVisibility(View.GONE);

        }else {
            final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                Intent i = new Intent(getActivity(), AddEnderecoActivity.class);
                startActivityForResult(i, GET_LOCATION_CODE);
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Seu GPS está desligado, você deseja ativar?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToVerDoacao(){
        Intent i = new Intent(getActivity(), VerDoacaoActivity.class);
        i.putExtra("id_doacao", doacao_editada.getObjectId());
        i.putExtra("id_doador", currentUser.getObjectId());
        getActivity().setIntent(i);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new VerDoacaoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
