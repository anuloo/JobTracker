package com.woonza.app.jobtracker;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 10/31/2016.
 */

public class FileChooser extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        //recursFiles(currentDir);
        fill(currentDir);
    }

    private void recursFiles(File dir){
        System.out.println("Now scanning " + dir.getAbsolutePath());
        System.out.println("Can r dir? " + dir.canRead());
        System.out.println("Can w dir? " + dir.canWrite());
        System.out.println("Can x dir? " + dir.canExecute());
        File[] listFiles = dir.listFiles();
        if(listFiles != null){
            for(File file : listFiles){
                System.out.println(file.getAbsolutePath());
                if(file.isDirectory()){
                    recursFiles(file);
                }
            }
        } else {
            System.out.println("That directory has nothing in it.");
        }
    }

    private void fill(File f)
    {
        File[]dirs = f.listFiles();
        this.setTitle("Current Dir: "+f.getName());
        List<Item> dir = new ArrayList<Item>();
        List<Item>fls = new ArrayList<Item>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                String fileExtention = ff.getName().substring(ff.getName().lastIndexOf('.') + 1);
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                     dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));

                }
                else
                {



                    if(fileExtention.toLowerCase().trim().contains("doc")||
                       fileExtention.toLowerCase().trim().contains("docx")){
                        fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_doc_icon"));
                    }else if(fileExtention.toLowerCase().trim().contains("pdf")){
                        fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_pdf_icon"));
                    }
                }
            }
        }catch(Exception e)
        {

        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        //System.out.println("-----------------------------f.getName() " + f.getName());
        if(!f.getName().trim().equalsIgnoreCase("")) {
            dir.add(0, new Item("..", "Parent Directory", "", f.getParent(), "directory_up"));
        }
        adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}