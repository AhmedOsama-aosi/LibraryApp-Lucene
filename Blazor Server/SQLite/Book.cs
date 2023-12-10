using System;
using System.Collections.Generic;

namespace LibraryApp.SQLite;

public partial class Book
{
    public long BookId { get; set; }

    public string? BookName { get; set; }

    public long? BookCategory { get; set; }

    public long? BookType { get; set; }

    public long? BookDate { get; set; }

    public string? Authors { get; set; }

    public long? MainAuthor { get; set; }

    public long? Printed { get; set; }

    public long? GroupId { get; set; }

    public long? Hidden { get; set; }

    public long? MajorOnline { get; set; }

    public long? MinorOnline { get; set; }

    public long? MajorOndisk { get; set; }

    public long? MinorOndisk { get; set; }

    public string? PdfLinks { get; set; }

    public long? PdfOndisk { get; set; }

    public long? PdfOnline { get; set; }

    public long? CoverOndisk { get; set; }

    public long? CoverOnline { get; set; }

    public string? MetaData { get; set; }

    public long? Parent { get; set; }
}
