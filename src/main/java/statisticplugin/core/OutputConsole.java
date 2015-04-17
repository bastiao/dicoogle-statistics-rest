package statisticplugin.core;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bastiao
 */
public class OutputConsole implements IOutput
{

    private List<IOutput> consoles = new ArrayList<IOutput>();
    
    
    public void addConsole(IOutput e)
    {
        consoles.add(e);
    }
    public void removeConsole(IOutput e)
    {
        consoles.remove(e);
    }
    

    @Override
    public void clear() {
        for (IOutput o : consoles)
        {
            o.clear();
        }
    }

    @Override
    public void addLine(String s) {
        for (IOutput o : consoles)
        {
            o.addLine(s);
        }
    }
    
}
