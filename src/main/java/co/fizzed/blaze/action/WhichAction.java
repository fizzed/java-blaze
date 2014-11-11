/*
 * Copyright 2014 Fizzed Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.fizzed.blaze.action;

import co.fizzed.blaze.core.Context;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author joelauer
 */
public class WhichAction extends Action<File> {
    
    private static final Map<String,File> cache = new ConcurrentHashMap<>();
    
    private String command;
    
    public WhichAction(Context context) {
        super(context, "which");
    }

    public String getCommand() {
        return command;
    }
    
    public WhichAction command(String command) {
        this.command = command;
        return this;
    }
    
    @Override
    protected Result<File> execute() throws Exception {
        File exeFile;
        
        // is it cached?
        if (cache.containsKey(command)) {
            exeFile = cache.get(command);
        } else {
            // search path for executable...
            exeFile = findExecutable(context, command);
            if (exeFile != null) {
                // cache result
                cache.put(command, exeFile);
            }
        }
        
        return new Result(exeFile);
    }

    static public File findExecutable(Context context, String command) throws Exception {
        // search PATH environment variable
        String path = System.getenv("PATH");
        if (path != null) {
            String[] paths = path.split(File.pathSeparator);
            for (String p : paths) {
                //System.out.println("searching: " + p);
                for (String ext : context.getSettings().getExecutableExtensions()) {
                    String commandWithExt = command + ext;
                    File f = new File(p, commandWithExt);
                    if (f.exists() && f.isFile() && f.canExecute()) {
                        return f;
                    }
                }
            }
        }
        
        return null;
    }
    
}