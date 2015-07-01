package estudoar.cin.ufpe.br.estudoar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by joaoveras on 06/06/2015.
 */
public class DoacaoAdapter extends ArrayAdapter {
    protected Context mContext;
    protected List mDoacoes;
    protected int mFilter;

    public DoacaoAdapter(Context context, List doacoes, int filter) {
        super(context, R.layout.materiais_custom, doacoes);
        mContext = context;
        mDoacoes = doacoes;
        mFilter = filter;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.materiais_custom, null);
            holder = new ViewHolder();
            holder.nomeDoacao = (TextView) convertView.findViewById(R.id.materialName);
            holder.descricaoDoacao = (TextView) convertView.findViewById(R.id.materialDesc);
            holder.categoriaDoacao = (TextView) convertView.findViewById(R.id.materialCateg);
            holder.imagemDoacao = (ImageView) convertView.findViewById(R.id.materialImage);
            holder.statusDoacao = (ImageView) convertView.findViewById(R.id.materialStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject doacaoObject = (ParseObject) mDoacoes.get(position);

        // pega o nome do material
        String username = doacaoObject.getString("nome");
        holder.nomeDoacao.setText(username);

        // pega a categoria do material
        String categoria = doacaoObject.getString("categoria");
        holder.categoriaDoacao.setText(categoria);

        // pega a descricao do material
        String descricao = doacaoObject.getString("descricao");
        holder.descricaoDoacao.setText(descricao);

        if(mFilter == 2) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Favoritos");
            query.whereEqualTo("doacao", doacaoObject.getObjectId());
            query.whereEqualTo("interessado", ParseUser.getCurrentUser().getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject favoritoObject, ParseException e) {
                    if (e == null) {
                        String status = favoritoObject.getString("status");
                        switch (status){
                            case "E":
                                holder.statusDoacao.setImageResource(R.drawable.ic_waitng);
                                break;
                            case "S":
                                holder.statusDoacao.setImageResource(R.drawable.ic_accepted);
                                break;
                            case "N":
                                holder.statusDoacao.setImageResource(R.drawable.ic_refused);
                                break;
                        }
                    }
                }
            });
        }else{
            holder.statusDoacao.setVisibility(View.GONE);
        }

        // pega a imagem do material
        ParseFile foto = (ParseFile) doacaoObject.get("foto");
        Log.d("foto", foto.getName() + " " + foto.getUrl());
        Picasso.with(mContext)
                .load(foto.getUrl())
                .resize(200, 200).centerCrop()
                .into(holder.imagemDoacao);

        return convertView;
    }

    public static class ViewHolder {
        TextView nomeDoacao;
        TextView descricaoDoacao;
        TextView categoriaDoacao;
        ImageView imagemDoacao;
        ImageView statusDoacao;
    }

}
