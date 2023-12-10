using System;
using System.Collections.Generic;

namespace LibraryApp.SQLite;

public partial class CoauthorBook
{
    public long? AuthorId { get; set; }

    public long? BookId { get; set; }
}
