using System.Text;

namespace LibraryApp.Shared
{
    public class CustomServices
    {
        static public string EnToAr(string str)
        {
            // Create a dictionary that maps the English digits to the Arabic digits
            Dictionary<char, char> map = new Dictionary<char, char>()
                                     {
                                       {'0', '\u0660'},
                                       {'1', '\u0661'},
                                       {'2', '\u0662'},
                                       {'3', '\u0663'},
                                       {'4', '\u0664'},
                                       {'5', '\u0665'},
                                       {'6', '\u0666'},
                                       {'7', '\u0667'},
                                       {'8', '\u0668'},
                                       {'9', '\u0669'}
                                     };

            // Create a string builder to store the output string
            StringBuilder sb = new StringBuilder();

            // Loop through each character in the input string
            foreach (char c in str)
            {
                // Check if the character is a digit
                if (char.IsDigit(c))
                {
                    // Replace the digit with its corresponding Arabic digit from the dictionary
                    sb.Append(map[c]);
                }
                else
                {
                    // Append the character as it is
                    sb.Append(c);
                }
            }

            // Return the output string
            return sb.ToString();
        }

        static public Task denger_alert(ref string ErrorMessage, Exception e, dynamic c, ref bool show)
        {
            ErrorMessage = e.Message;
            show = !show;
            Task.Delay(5500).Wait();
            show = !show;
          return Task.Run(() =>
           {
               c.StateHasChanged();

           });
        }
    }
}
